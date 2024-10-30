package com.ptit.datn.repository;

import com.ptit.datn.domain.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findAllByProvinceIdOrderByNameAsc(Long provinceId);
}
