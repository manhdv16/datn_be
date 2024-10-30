package com.ptit.datn.web.rest;

import com.ptit.datn.domain.Ward;
import com.ptit.datn.repository.WardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wards")
public class WardResource {

    private final WardRepository wardRepository;

    public WardResource(WardRepository wardRepository) {
        this.wardRepository = wardRepository;
    }

    @GetMapping
    public ResponseEntity<List<Ward>> getWardsByDistrictId(@RequestParam(name = "districtId") Long districtId) {
        return ResponseEntity.ok().body(wardRepository.findAllByDistrictIdOrderByNameAsc(districtId));
    }
}
