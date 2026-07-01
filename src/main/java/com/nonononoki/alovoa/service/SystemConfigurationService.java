package com.nonononoki.alovoa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nonononoki.alovoa.entity.SystemConfiguration;
import com.nonononoki.alovoa.repo.SystemConfigurationRepository;

/**
 * Loads and updates the runtime-configurable platform settings (paid access
 * switch, activation price, currency). Falls back to the values defined in
 * {@code application.properties} the first time the application starts.
 */
@Service
public class SystemConfigurationService {

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepo;

    @Value("${app.access.payment.enabled}")
    private boolean defaultPaymentEnabled;

    @Value("${app.access.price}")
    private double defaultAccessPrice;

    @Value("${app.access.currency}")
    private String defaultCurrency;

    public SystemConfiguration get() {
        return systemConfigurationRepo.findById(SystemConfiguration.SINGLETON_ID)
                .orElseGet(this::createDefault);
    }

    public SystemConfiguration createDefault() {
        SystemConfiguration config = new SystemConfiguration();
        config.setId(SystemConfiguration.SINGLETON_ID);
        config.setPaymentEnabled(defaultPaymentEnabled);
        config.setAccessPrice(defaultAccessPrice);
        config.setCurrency(defaultCurrency);
        return systemConfigurationRepo.saveAndFlush(config);
    }

    public SystemConfiguration update(boolean paymentEnabled, double accessPrice, String currency) {
        SystemConfiguration config = get();
        config.setPaymentEnabled(paymentEnabled);
        config.setAccessPrice(accessPrice);
        config.setCurrency(currency);
        return systemConfigurationRepo.saveAndFlush(config);
    }
}
