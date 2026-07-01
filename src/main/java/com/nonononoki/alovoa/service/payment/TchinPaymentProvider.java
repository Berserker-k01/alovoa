package com.nonononoki.alovoa.service.payment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nonononoki.alovoa.entity.user.UserPayment;
import com.nonononoki.alovoa.model.payment.PaymentCallback;
import com.nonononoki.alovoa.model.payment.PaymentInitResult;

/**
 * Tchin Mobile Money provider integration.
 *
 * NOTE: The exact request/response shape and signature algorithm below follow
 * the common Tchin / Mobile Money aggregator convention (JSON checkout + HMAC
 * SHA-256 signed webhook). Adjust the field names and endpoints in
 * {@link #initiatePayment} and {@link #parseAndVerifyWebhook} to match the
 * official Tchin API documentation once the credentials are wired in
 * {@code application.properties} (app.payment.tchin.*).
 */
@Component
public class TchinPaymentProvider implements PaymentProvider {

    public static final String NAME = "tchin";

    private static final Logger logger = LoggerFactory.getLogger(TchinPaymentProvider.class);

    @Value("${app.payment.tchin.base-url}")
    private String baseUrl;

    @Value("${app.payment.tchin.api-key}")
    private String apiKey;

    @Value("${app.payment.tchin.secret}")
    private String secret;

    @Value("${app.payment.tchin.webhook-secret}")
    private String webhookSecret;

    @Value("${app.payment.tchin.return-url}")
    private String returnUrl;

    @Value("${app.payment.tchin.callback-url}")
    private String callbackUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public PaymentInitResult initiatePayment(UserPayment payment) throws Exception {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", payment.getAmount());
        body.put("currency", payment.getCurrency());
        body.put("reference", payment.getReference());
        body.put("channel", payment.getChannel());
        body.put("return_url", returnUrl);
        body.put("callback_url", callbackUrl);

        String json = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/payments"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("X-Signature", sign(json))
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Tchin payment initiation failed: HTTP " + response.statusCode()
                    + " - " + response.body());
        }

        JsonNode node = objectMapper.readTree(response.body());
        String checkoutUrl = text(node, "checkout_url", "payment_url", "url");
        String transactionId = text(node, "transaction_id", "id", "token");

        return PaymentInitResult.builder()
                .checkoutUrl(checkoutUrl)
                .providerTransactionId(transactionId)
                .build();
    }

    @Override
    public PaymentCallback parseAndVerifyWebhook(String rawBody, String signature) {
        try {
            String expected = sign(rawBody);
            if (signature == null || !constantTimeEquals(expected, signature)) {
                logger.warn("Tchin webhook signature mismatch");
                return null;
            }

            JsonNode node = objectMapper.readTree(rawBody);
            String reference = text(node, "reference", "merchant_reference");
            String transactionId = text(node, "transaction_id", "id");
            String status = text(node, "status", "state");

            PaymentCallback callback = new PaymentCallback();
            callback.setReference(reference);
            callback.setProviderTransactionId(transactionId);
            callback.setSuccess(status != null
                    && (status.equalsIgnoreCase("success") || status.equalsIgnoreCase("successful")
                    || status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("paid")));
            return callback;
        } catch (Exception e) {
            logger.error("Unable to parse Tchin webhook", e);
            return null;
        }
    }

    private String sign(String payload) throws Exception {
        String key = (webhookSecret != null && !webhookSecret.isBlank()) ? webhookSecret : secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(raw.length * 2);
        for (byte b : raw) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String text(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
            JsonNode data = node.get("data");
            if (data != null) {
                JsonNode nested = data.get(key);
                if (nested != null && !nested.isNull()) {
                    return nested.asText();
                }
            }
        }
        return null;
    }
}
