package com.nonononoki.alovoa.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nonononoki.alovoa.entity.SystemConfiguration;
import com.nonononoki.alovoa.entity.User;
import com.nonononoki.alovoa.entity.user.UserPayment;
import com.nonononoki.alovoa.model.AlovoaException;
import com.nonononoki.alovoa.model.payment.PaymentCallback;
import com.nonononoki.alovoa.model.payment.PaymentInitResult;
import com.nonononoki.alovoa.repo.UserPaymentRepository;
import com.nonononoki.alovoa.repo.UserRepository;
import com.nonononoki.alovoa.service.payment.PaymentProvider;
import com.nonononoki.alovoa.service.payment.TchinPaymentProvider;

/**
 * Orchestrates the account-activation payment flow (Mobile Money via Tchin):
 * initiating payments, handling provider webhooks and granting access.
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserPaymentRepository userPaymentRepo;

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @Autowired
    private List<PaymentProvider> providers;

    /**
     * Whether the given user is allowed to use the platform. Access is granted
     * when paid access is globally disabled, when the user is an admin, or when
     * the user has already paid the activation fee.
     */
    public boolean hasAccess(User user) {
        if (user == null) {
            return false;
        }
        if (user.isAdmin()) {
            return true;
        }
        SystemConfiguration config = systemConfigurationService.get();
        if (!config.isPaymentEnabled()) {
            return true;
        }
        return user.isAccessPaid();
    }

    /**
     * Creates a pending payment and asks the provider for a checkout URL.
     *
     * @param channel mobile money channel chosen by the user (wave, mtn, ...)
     */
    public PaymentInitResult initiatePayment(String channel) throws Exception {
        User user = authService.getCurrentUser(true);

        if (user.isAccessPaid()) {
            throw new AlovoaException("access_already_paid");
        }

        SystemConfiguration config = systemConfigurationService.get();
        if (!config.isPaymentEnabled()) {
            throw new AlovoaException("payment_disabled");
        }

        PaymentProvider provider = getProvider(TchinPaymentProvider.NAME);

        UserPayment payment = new UserPayment();
        payment.setUser(user);
        payment.setReference(UUID.randomUUID().toString());
        payment.setProvider(provider.getName());
        payment.setChannel(channel);
        payment.setAmount(config.getAccessPrice());
        payment.setCurrency(config.getCurrency());
        payment.setStatus(UserPayment.STATUS_PENDING);
        payment.setDateCreated(new Date());
        payment.setDateUpdated(new Date());
        payment = userPaymentRepo.saveAndFlush(payment);

        PaymentInitResult result = provider.initiatePayment(payment);

        if (result.getProviderTransactionId() != null) {
            payment.setProviderTransactionId(result.getProviderTransactionId());
            userPaymentRepo.saveAndFlush(payment);
        }

        return result;
    }

    /**
     * Processes an incoming provider webhook: verifies it, marks the payment and
     * grants access to the user on success.
     */
    public void handleWebhook(String providerName, String rawBody, String signature) {
        PaymentProvider provider = getProvider(providerName);
        if (provider == null) {
            logger.warn("Unknown payment provider in webhook: {}", providerName);
            return;
        }

        PaymentCallback callback = provider.parseAndVerifyWebhook(rawBody, signature);
        if (callback == null || callback.getReference() == null) {
            logger.warn("Invalid or unverifiable webhook for provider {}", providerName);
            return;
        }

        UserPayment payment = userPaymentRepo.findByReference(callback.getReference());
        if (payment == null) {
            logger.warn("No payment found for reference {}", callback.getReference());
            return;
        }

        payment.setProviderTransactionId(callback.getProviderTransactionId());
        payment.setDateUpdated(new Date());

        if (callback.isSuccess()) {
            payment.setStatus(UserPayment.STATUS_SUCCESS);
            grantAccess(payment.getUser());
        } else {
            payment.setStatus(UserPayment.STATUS_FAILED);
        }
        userPaymentRepo.saveAndFlush(payment);
    }

    public void grantAccess(User user) {
        if (user != null && !user.isAccessPaid()) {
            user.setAccessPaid(true);
            user.setAccessGrantedDate(new Date());
            userRepo.saveAndFlush(user);
        }
    }

    private PaymentProvider getProvider(String name) {
        if (name == null) {
            return null;
        }
        return providers.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
