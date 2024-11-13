package com.ptit.datn.repository;

import com.ptit.datn.domain.UserBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBuildingRepository extends JpaRepository<UserBuilding, Long> {
}
