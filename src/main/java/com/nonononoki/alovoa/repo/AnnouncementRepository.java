package com.nonononoki.alovoa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonononoki.alovoa.entity.Announcement;
import com.nonononoki.alovoa.entity.User;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByActiveTrueOrderByDateCreatedDesc();

    List<Announcement> findByActiveTrueAndCategoryOrderByDateCreatedDesc(String category);

    List<Announcement> findByUserOrderByDateCreatedDesc(User user);

    List<Announcement> findAllByOrderByDateCreatedDesc();

    long countByActiveTrue();
}
