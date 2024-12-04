package com.ptit.datn.repository;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.repository.custom.ContractRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<ContractEntity, Long>, ContractRepositoryCustom {
    Optional<ContractEntity> findByIdAndIsActiveTrue(Long id);

    @Query("""
        select count(c) from ContractEntity c where month(c.createdDate) = month(current_date) and c.isActive = true
    """)
    Integer countContractPerDay();
}
