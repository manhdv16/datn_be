package com.ptit.datn.web.rest;

import com.ptit.datn.service.BuildingService;
import com.ptit.datn.service.dto.BuildingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/buildings")
public class BuildingResource {

    private static final Logger log = LoggerFactory.getLogger(BuildingResource.class);

    private final BuildingService buildingService;

    public BuildingResource(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<Page<BuildingDTO>> getBuildings(@RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "20") Integer size,
                                                          @RequestParam(required = false) String keyword,
                                                          @RequestParam(required = false) Long wardId,
                                                          @RequestParam(required = false) Long districtId,
                                                          @RequestParam(required = false) Long provinceId,
                                                          @RequestParam(required = false) BigInteger minPrice,
                                                          @RequestParam(required = false) BigInteger maxPrice,
                                                          @RequestParam(required = false) Double minArea,
                                                          @RequestParam(required = false) Double maxArea) {
        log.info("Get buildings");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(buildingService.getBuildings(pageable, keyword, wardId, districtId,
            provinceId, minPrice, maxPrice, minArea, maxArea));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingDTO> getBuilding(@PathVariable Long id) {
        log.info("Get building");
        BuildingDTO buildingDTO = buildingService.getBuildingById(id);
        if (buildingDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(buildingDTO);
    }

    @PostMapping
    public ResponseEntity<BuildingDTO> createBuilding(@RequestBody BuildingDTO buildingDTO) {
        log.info("Create building");
        return ResponseEntity.created(null).body(buildingService.createBuilding(buildingDTO));
    }

    @PutMapping
    public ResponseEntity<BuildingDTO> updateBuilding(@RequestBody BuildingDTO buildingDTO) {
        log.info("Update building");
        return ResponseEntity.ok(buildingService.updateBuilding(buildingDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        log.info("Delete building");
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
