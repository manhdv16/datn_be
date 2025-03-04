package com.ptit.datn.repository;

import com.ptit.datn.domain.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    List<Ward> findAllByDistrictIdOrderByNameAsc(Long districtId);
}
