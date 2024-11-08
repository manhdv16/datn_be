package com.ptit.datn.web.rest;

import com.ptit.datn.dto.request.BuildingCreateRequest;
import com.ptit.datn.service.BuildingService;
import com.ptit.datn.service.dto.BuildingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

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

    @GetMapping("/all")
    public ResponseEntity<List<BuildingDTO>> getAllBuildings() {
        log.info("Get all buildings");
        return ResponseEntity.ok().body(buildingService.getAllBuildingsUnpaged());
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BuildingDTO> createBuilding(@RequestPart(name = "data") BuildingCreateRequest buildingPersistRequest,
                                                      @RequestPart(name = "images", required = false) MultipartFile[] images) {
        log.info("Create building");
        return ResponseEntity.created(null).body(buildingService.createBuilding(buildingPersistRequest, images));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BuildingDTO> updateBuilding(@RequestPart(name = "data") BuildingDTO buildingDTO,
                                                      @RequestPart(name = "images", required = false) MultipartFile[] images) {
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
