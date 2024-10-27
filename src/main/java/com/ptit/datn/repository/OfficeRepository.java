package com.ptit.datn.repository;

import com.ptit.datn.domain.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long>, JpaSpecificationExecutor<Office> {
    void deleteAllByBuildingId(Long buildingId);
}
