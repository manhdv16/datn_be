package com.ptit.datn.repository;

import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.repository.custom.ContractRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<ContractEntity, Long>, ContractRepositoryCustom {
    Optional<ContractEntity> findByIdAndIsActiveTrue(Long id);
}
