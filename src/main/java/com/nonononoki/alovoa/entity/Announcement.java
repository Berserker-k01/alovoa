package com.nonononoki.alovoa.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * A community announcement (party, friends, dating, outing, local event...)
 * posted by a user. Announcements are filtered by location and category.
 */
@Getter
@Setter
@Entity
public class Announcement {

    public static final String CATEGORY_PARTY = "party";
    public static final String CATEGORY_FRIENDS = "friends";
    public static final String CATEGORY_DATING = "dating";
    public static final String CATEGORY_DISCUSSION = "discussion";
    public static final String CATEGORY_ACTIVITY = "activity";
    public static final String CATEGORY_EVENT = "event";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    private String category;

    private String title;

    @Column(length = 1000)
    private String text;

    /** Optional human-readable place / city. */
    private String place;

    private Double locationLatitude;

    private Double locationLongitude;

    /** Optional date/time of the event the announcement is about. */
    private Date eventDate;

    private Date dateCreated;

    /** Set to false when hidden/moderated by an admin. */
    private boolean active = true;
}
