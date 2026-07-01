package com.nonononoki.alovoa.model;

import java.util.Date;

import com.nonononoki.alovoa.entity.Announcement;

import lombok.Builder;
import lombok.Data;

/**
 * View representation of an {@link Announcement} including the distance to the
 * requesting user. The author identity is reduced to the anonymous pseudo.
 */
@Data
@Builder
public class AnnouncementDto {

    private Long id;
    private String category;
    private String title;
    private String text;
    private String place;
    private Date eventDate;
    private Date dateCreated;
    private Integer distanceKm;
    /** Anonymous author pseudo. */
    private String authorName;
    private boolean ownAnnouncement;
}
