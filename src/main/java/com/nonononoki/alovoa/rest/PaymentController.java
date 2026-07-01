package com.nonononoki.alovoa.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nonononoki.alovoa.model.payment.PaymentInitResult;
import com.nonononoki.alovoa.service.PaymentService;

/**
 * Endpoints for the account-activation payment flow (Mobile Money via Tchin).
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * Starts a payment for the current user and returns the checkout URL the
     * front-end should redirect to.
     */
    @PostMapping("/initiate")
    @ResponseBody
    public PaymentInitResult initiate(@RequestParam(defaultValue = "wave") String channel) throws Exception {
        return paymentService.initiatePayment(channel);
    }

    /**
     * Provider webhook. The signature header is verified inside the provider.
     */
    @PostMapping("/webhook/{provider}")
    public ResponseEntity<String> webhook(@PathVariable String provider,
                                          @RequestBody(required = false) String rawBody,
                                          @RequestHeader(value = "X-Signature", required = false) String signature) {
        logger.info("Received payment webhook for provider {}", provider);
        paymentService.handleWebhook(provider, rawBody, signature);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
