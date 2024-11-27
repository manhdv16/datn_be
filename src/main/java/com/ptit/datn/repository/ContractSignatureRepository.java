package com.ptit.datn.repository;

import com.ptit.datn.domain.ContractSignatureEntity;
import com.ptit.datn.service.dto.UserNameDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContractSignatureRepository extends JpaRepository<ContractSignatureEntity, Long> {

    @Query("select count(*) from ContractSignatureEntity c where c.step is not null")
    Integer countStep();
}
