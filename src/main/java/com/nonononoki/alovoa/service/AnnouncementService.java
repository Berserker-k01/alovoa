package com.nonononoki.alovoa.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nonononoki.alovoa.Tools;
import com.nonononoki.alovoa.entity.Announcement;
import com.nonononoki.alovoa.entity.User;
import com.nonononoki.alovoa.model.AlovoaException;
import com.nonononoki.alovoa.model.AnnouncementDto;
import com.nonononoki.alovoa.repo.AnnouncementRepository;

/**
 * Creates and queries community announcements, applying proximity filtering
 * based on the requesting user's location.
 */
@Service
public class AnnouncementService {

    @Autowired
    private AuthService authService;

    @Autowired
    private AnnouncementRepository announcementRepo;

    @Value("${app.announcement.title.max}")
    private int titleMax;

    @Value("${app.announcement.text.max}")
    private int textMax;

    @Value("${app.announcement.max.distance}")
    private int maxDistance;

    @Value("${app.announcement.user.max}")
    private int userMax;

    public Announcement create(String category, String title, String text, String place, Date eventDate)
            throws AlovoaException {
        User user = authService.getCurrentUser(true);

        if (title == null || title.isBlank() || text == null || text.isBlank() || category == null
                || category.isBlank()) {
            throw new AlovoaException("announcement_invalid");
        }
        if (title.length() > titleMax || text.length() > textMax) {
            throw new AlovoaException("announcement_too_long");
        }
        if (announcementRepo.findByUserOrderByDateCreatedDesc(user).size() >= userMax) {
            throw new AlovoaException("announcement_max_reached");
        }

        Announcement announcement = new Announcement();
        announcement.setUser(user);
        announcement.setCategory(category);
        announcement.setTitle(title.strip());
        announcement.setText(text.strip());
        announcement.setPlace(place);
        announcement.setEventDate(eventDate);
        announcement.setLocationLatitude(user.getLocationLatitude());
        announcement.setLocationLongitude(user.getLocationLongitude());
        announcement.setDateCreated(new Date());
        announcement.setActive(true);
        return announcementRepo.saveAndFlush(announcement);
    }

    /**
     * Lists active announcements near the current user, optionally filtered by
     * category, sorted by proximity then recency.
     */
    public List<AnnouncementDto> findNearby(String category) throws AlovoaException {
        User user = authService.getCurrentUser(true);

        List<Announcement> announcements = (category == null || category.isBlank())
                ? announcementRepo.findByActiveTrueOrderByDateCreatedDesc()
                : announcementRepo.findByActiveTrueAndCategoryOrderByDateCreatedDesc(category);

        List<AnnouncementDto> dtos = new ArrayList<>();
        for (Announcement a : announcements) {
            Integer distance = distanceFor(user, a);
            if (distance != null && distance > maxDistance) {
                continue;
            }
            dtos.add(toDto(a, user, distance));
        }

        dtos.sort(Comparator.comparing((AnnouncementDto d) -> d.getDistanceKm() == null ? Integer.MAX_VALUE : d.getDistanceKm())
                .thenComparing(d -> d.getDateCreated() == null ? new Date(0) : d.getDateCreated(), Comparator.reverseOrder()));
        return dtos;
    }

    public List<AnnouncementDto> findOwn() throws AlovoaException {
        User user = authService.getCurrentUser(true);
        List<AnnouncementDto> dtos = new ArrayList<>();
        for (Announcement a : announcementRepo.findByUserOrderByDateCreatedDesc(user)) {
            dtos.add(toDto(a, user, distanceFor(user, a)));
        }
        return dtos;
    }

    public void delete(Long id) throws AlovoaException {
        User user = authService.getCurrentUser(true);
        Announcement announcement = announcementRepo.findById(id)
                .orElseThrow(() -> new AlovoaException("announcement_not_found"));
        if (!announcement.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new AlovoaException("not_authorized");
        }
        announcementRepo.delete(announcement);
    }

    private Integer distanceFor(User user, Announcement a) {
        if (user.getLocationLatitude() == null || a.getLocationLatitude() == null) {
            return null;
        }
        return Tools.calcDistanceKm(user.getLocationLatitude(), user.getLocationLongitude(),
                a.getLocationLatitude(), a.getLocationLongitude());
    }

    private AnnouncementDto toDto(Announcement a, User user, Integer distance) {
        return AnnouncementDto.builder()
                .id(a.getId())
                .category(a.getCategory())
                .title(a.getTitle())
                .text(a.getText())
                .place(a.getPlace())
                .eventDate(a.getEventDate())
                .dateCreated(a.getDateCreated())
                .distanceKm(distance)
                .authorName(a.getUser() != null ? a.getUser().getFirstName() : null)
                .ownAnnouncement(a.getUser() != null && a.getUser().getId().equals(user.getId()))
                .build();
    }
}
