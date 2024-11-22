package com.ptit.datn.repository;

import com.ptit.datn.domain.UserBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBuildingRepository extends JpaRepository<UserBuilding, Long> {
    @Query("SELECT COUNT(ub) FROM UserBuilding ub WHERE ub.buildingId = :buildingId")
    Integer countUserManagerByBuildingId(@Param("buildingId") Long buildingId);
}
