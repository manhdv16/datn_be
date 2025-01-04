package com.ptit.datn.repository;

import com.ptit.datn.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.status = :status")
    Page<Notification> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId")
    Page<Notification> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.status = :status")
    List<Notification> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

}
