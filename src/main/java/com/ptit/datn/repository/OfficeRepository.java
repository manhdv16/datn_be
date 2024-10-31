package com.ptit.datn.repository;

import com.ptit.datn.domain.Office;
import com.ptit.datn.service.dto.BuildingNameDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long>, JpaSpecificationExecutor<Office> {
    void deleteAllByBuildingId(Long buildingId);

    List<Office> findAllByBuildingId(Long buildingId);

    @Query("select new com.ptit.datn.service.dto.BuildingNameDTO(o.building.id, o.building.name) from Office o where o.id = :officeId")
    Optional<BuildingNameDTO> findBuildingByOfficeId(@Param("officeId") Long officeId);

    @Query("select o from Office o where o.id in :ids")
    List<Office> findAllByIds(@Param("ids") Set<Long> ids);
}
