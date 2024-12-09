package com.ptit.datn.repository;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.repository.custom.ContractRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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
}
