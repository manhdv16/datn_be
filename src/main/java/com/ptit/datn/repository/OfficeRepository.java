package com.ptit.datn.repository;

import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Office;
import com.ptit.datn.service.dto.BuildingDTO;
import com.ptit.datn.service.dto.BuildingNameDTO;
import com.ptit.datn.service.dto.OfficeDTO;
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

    @Query("select new com.ptit.datn.service.dto.BuildingDTO(o.building) from Office o where o.id = :officeId")
    Optional<BuildingDTO> findBuildingByOfficeId(@Param("officeId") Long officeId);

    @Query("select o from Office o where o.id in :ids")
    List<Office> findAllByIds(@Param("ids") Set<Long> ids);

    @Query("select new com.ptit.datn.service.dto.OfficeDTO(o) from Office o " +
        "join ContractOfficeEntity co on co.officeId = o.id " +
        "where co.contractId = :contractId")
    List<OfficeDTO> findByContractId(@Param("contractId") Long contractId);
}
