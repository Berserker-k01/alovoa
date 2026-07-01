package com.nonononoki.alovoa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nonononoki.alovoa.entity.User;
import com.nonononoki.alovoa.entity.user.UserPayment;

public interface UserPaymentRepository extends JpaRepository<UserPayment, Long> {

    UserPayment findByReference(String reference);

    List<UserPayment> findByUser(User user);

    List<UserPayment> findByStatusOrderByDateCreatedDesc(String status);

    List<UserPayment> findAllByOrderByDateCreatedDesc();

    long countByStatus(String status);
}
