package com.ptit.datn.web.rest;

import com.ptit.datn.domain.Province;
import com.ptit.datn.repository.ProvinceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/provinces")
public class ProvinceResource {

    private final ProvinceRepository provinceRepository;

    public ProvinceResource(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    @GetMapping
    public ResponseEntity<List<Province>> getProvinces() {
        return ResponseEntity.ok().body(provinceRepository.findAllByOrderByNameAsc());
    }
}
