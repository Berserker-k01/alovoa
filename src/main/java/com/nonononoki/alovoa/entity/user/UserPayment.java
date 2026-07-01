package com.nonononoki.alovoa.entity.user;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nonononoki.alovoa.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Tracks an account-activation payment performed through a Mobile Money
 * provider (Tchin). One successful payment grants the user access.
 */
@Getter
@Setter
@Entity
public class UserPayment {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    /** Local reference sent to the provider, used to reconcile webhooks. */
    private String reference;

    /** Provider-side transaction id (filled once known). */
    private String providerTransactionId;

    /** Payment provider, e.g. "tchin". */
    private String provider;

    /** Mobile money channel chosen by the user (wave, mtn, moov, orange...). */
    private String channel;

    private double amount;

    private String currency;

    private String status;

    private Date dateCreated;

    private Date dateUpdated;
}
