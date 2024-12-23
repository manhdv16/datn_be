package com.ptit.datn.repository;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.dto.response.StatisticsContractResponse;
import com.ptit.datn.repository.custom.ContractRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<ContractEntity, Long>, ContractRepositoryCustom {
    Optional<ContractEntity> findByIdAndIsActiveTrue(Long id);

    @Query("""
        select count(c) from ContractEntity c where month(c.createdDate) = month(current_date) and c.isActive = true
    """)
    Integer countContractPerDay();

    @Query("select c.startDate from ContractEntity c where c.isActive = true order by c.startDate limit 1")
    LocalDate getMinStartDate();

    @Query("select c.endDate from ContractEntity c where c.isActive = true order by c.endDate desc limit 1")
    LocalDate getMaxEndDate();

    @Query(value = """
        SELECT
            DATE(c.created_date) AS createdDate,
            COUNT(c.id) AS numberOfContracts
        FROM building b
        LEFT JOIN office o ON o.building_id = b.id
        LEFT JOIN contract_office co ON co.office_id = o.id
        LEFT JOIN contract c ON c.id = co.contract_id
        WHERE
            b.id = :buildingId
            AND c.created_date >= :startTime
            AND c.created_date <= :endTime
            AND c.is_active = true
        GROUP BY DATE(c.created_date)
        ORDER BY DATE(c.created_date) ASC
        """, nativeQuery = true)
    List<Object[]> findContractStatistics(@Param("buildingId") Long buildingId,
        @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);
}

