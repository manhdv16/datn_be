package com.ptit.datn.repository;

import com.ptit.datn.domain.UserBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBuildingRepository extends JpaRepository<UserBuilding, Long> {
    @Query("SELECT COUNT(ub) FROM UserBuilding ub WHERE ub.buildingId = :buildingId")
    Integer countUserManagerByBuildingId(@Param("buildingId") Long buildingId);

    @Query("SELECT COUNT(ub) FROM UserBuilding ub WHERE ub.buildingId = :buildingId AND ub.userId = :userId")
    Integer countUserManagerByBuildingIdAndUserId(@Param("buildingId") Long buildingId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserBuilding ub WHERE ub.userId = :userId AND ub.buildingId = :buildingId")
    void deleteByBuildingIdAndUserId(Long buildingId, Long userId);

    List<UserBuilding> findByUserId(Long userId);
}
