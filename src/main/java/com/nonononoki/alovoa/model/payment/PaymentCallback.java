package com.nonononoki.alovoa.model.payment;

import lombok.Data;

/**
 * Normalized representation of a payment provider webhook/callback.
 */
@Data
public class PaymentCallback {

    /** Local reference we generated when initiating the payment. */
    private String reference;

    /** Provider-side transaction id. */
    private String providerTransactionId;

    /** Whether the payment succeeded. */
    private boolean success;
}
