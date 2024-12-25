package com.ptit.datn.repository;

import com.ptit.datn.domain.ContractOfficeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractOfficeRepository extends JpaRepository<ContractOfficeEntity, Long> {
    List<ContractOfficeEntity> findByContractId(Long contractId);
}
