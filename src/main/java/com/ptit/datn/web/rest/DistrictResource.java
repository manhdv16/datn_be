package com.ptit.datn.web.rest;

import com.ptit.datn.domain.District;
import com.ptit.datn.repository.DistrictRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/districts")
public class DistrictResource {

    private final DistrictRepository districtRepository;

    public DistrictResource(DistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }

    @GetMapping
    public ResponseEntity<List<District>> getDistrictsByProvinceId(@RequestParam(name = "provinceId") Long provinceId) {
        return ResponseEntity.ok().body(districtRepository.findAllByProvinceIdOrderByNameAsc(provinceId));
    }
}
