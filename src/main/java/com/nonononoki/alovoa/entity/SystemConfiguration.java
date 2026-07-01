package com.nonononoki.alovoa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Singleton-style entity holding runtime-configurable platform settings.
 * Only a single row (id = {@link #SINGLETON_ID}) is expected to exist.
 * The admin panel edits these values without restarting the application.
 */
@Getter
@Setter
@Entity
public class SystemConfiguration {

    public static final long SINGLETON_ID = 1L;

    @Id
    private Long id = SINGLETON_ID;

    /** Whether paid access (account activation fee) is enforced. */
    private boolean paymentEnabled;

    /** Price required to activate an account. */
    private double accessPrice;

    /** ISO currency code (e.g. XOF for FCFA). */
    private String currency;
}
