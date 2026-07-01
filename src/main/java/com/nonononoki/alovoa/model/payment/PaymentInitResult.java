package com.nonononoki.alovoa.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result returned by a payment provider when a payment is initiated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitResult {

    /** URL where the user must be redirected to complete the Mobile Money payment. */
    private String checkoutUrl;

    /** Provider-side transaction id, if available immediately. */
    private String providerTransactionId;
}
