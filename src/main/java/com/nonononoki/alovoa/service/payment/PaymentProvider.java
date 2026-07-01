package com.nonononoki.alovoa.service.payment;

import com.nonononoki.alovoa.entity.user.UserPayment;
import com.nonononoki.alovoa.model.payment.PaymentCallback;
import com.nonononoki.alovoa.model.payment.PaymentInitResult;

/**
 * Abstraction over a Mobile Money payment provider so that EyaLove is not
 * tied to a single integration. The default implementation targets Tchin
 * (Wave, MTN, Moov, Tmoney, Orange...), but additional providers can be added
 * by implementing this interface.
 */
public interface PaymentProvider {

    /** Unique identifier, e.g. "tchin". Matches {@link UserPayment#getProvider()}. */
    String getName();

    /**
     * Starts a payment on the provider side.
     *
     * @param payment the local pending payment (already persisted) to charge
     * @return checkout information the user is redirected to
     */
    PaymentInitResult initiatePayment(UserPayment payment) throws Exception;

    /**
     * Verifies the authenticity of an incoming webhook and converts it into a
     * normalized {@link PaymentCallback}.
     *
     * @param rawBody   raw request body sent by the provider
     * @param signature signature header sent by the provider (may be null)
     * @return the parsed callback, or null if the signature is invalid
     */
    PaymentCallback parseAndVerifyWebhook(String rawBody, String signature);
}
