package com.ptit.datn.repository;

import com.ptit.datn.domain.ServiceTypeEntity;
import com.ptit.datn.repository.custom.ServiceTypeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepository extends JpaRepository<ServiceTypeEntity, Integer>, ServiceTypeRepositoryCustom {

}
