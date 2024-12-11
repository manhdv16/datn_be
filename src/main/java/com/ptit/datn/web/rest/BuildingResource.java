package com.ptit.datn.web.rest;

import com.ptit.datn.dto.request.BuildingCreateRequest;
import com.ptit.datn.dto.request.BuildingUpdateRequest;
import com.ptit.datn.dto.response.ApiResponse;
import com.ptit.datn.service.BuildingService;
import com.ptit.datn.service.dto.BuildingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ApiResponse<Page<BuildingDTO>> getBuildings(@RequestParam(defaultValue = "0") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long wardId,
                                                       @RequestParam(required = false) Long districtId,
                                                       @RequestParam(required = false) Long provinceId,
                                                       @RequestParam(required = false) BigInteger minPrice,
                                                       @RequestParam(required = false) BigInteger maxPrice,
                                                       @RequestParam(required = false) Double minArea,
                                                       @RequestParam(required = false) Double maxArea) {
        log.info("GET buildings START");
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        try {
            ApiResponse<Page<BuildingDTO>> response = ApiResponse.<Page<BuildingDTO>>builder()
                .code(200)
                .message("Lấy danh sách tòa nhà thành công")
                .result(buildingService.getBuildings(pageable, keyword, wardId, districtId,
                    provinceId, minPrice, maxPrice, minArea, maxArea))
                .build();
            log.info("GET buildings SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("GET buildings ERROR: ", e);
            return ApiResponse.<Page<BuildingDTO>>builder()
                .code(400)
                .message("Lấy danh sách tòa nhà thất bại")
                .result(null)
                .build();
        }
    }

    @GetMapping("/manage-list")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ApiResponse<Page<BuildingDTO>> getManageBuildings(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size,
                                                             @RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) Long wardId,
                                                             @RequestParam(required = false) Long districtId,
                                                             @RequestParam(required = false) Long provinceId,
                                                             @RequestParam(required = false) BigInteger minPrice,
                                                             @RequestParam(required = false) BigInteger maxPrice,
                                                             @RequestParam(required = false) Double minArea,
                                                             @RequestParam(required = false) Double maxArea) {
        log.info("GET buildings for manager START");
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        try {
            ApiResponse<Page<BuildingDTO>> response = ApiResponse.<Page<BuildingDTO>>builder()
                .code(200)
                .message("Lấy danh sách tòa nhà thành công")
                .result(buildingService.getBuildingsForManage(pageable, keyword, wardId, districtId,
                    provinceId, minPrice, maxPrice, minArea, maxArea))
                .build();
            log.info("GET buildings for manager SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("GET buildings for manager ERROR: ", e);
            return ApiResponse.<Page<BuildingDTO>>builder()
                .code(400)
                .message("Lấy danh sách tòa nhà thất bại")
                .result(null)
                .build();
        }
    }

    @GetMapping("/all")
    public ApiResponse<List<BuildingDTO>> getAllBuildings() {
        log.info("GET all buildings START");
        try {
            ApiResponse<List<BuildingDTO>> response = ApiResponse.<List<BuildingDTO>>builder()
                .code(200)
                .message("Lấy danh sách tòa nhà thành công")
                .result(buildingService.getAllBuildingsUnpaged())
                .build();
            log.info("GET all buildings SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("GET all buildings ERROR: ", e);
            return ApiResponse.<List<BuildingDTO>>builder()
                .code(400)
                .message("Lấy danh sách tòa nhà thất bại")
                .result(null)
                .build();
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<BuildingDTO> getBuilding(@PathVariable Long id) {
        log.info("GET building START");
        try {
            BuildingDTO buildingDTO = buildingService.getBuildingById(id);
            if (buildingDTO == null) {
                log.error("GET building ERROR: Building not found");
                return ApiResponse.<BuildingDTO>builder()
                    .code(404)
                    .message("Không tìm thấy tòa nhà")
                    .result(null)
                    .build();
            }
            ApiResponse<BuildingDTO> response = ApiResponse.<BuildingDTO>builder()
                .code(200)
                .message("Lấy thông tin tòa nhà thành công")
                .result(buildingDTO)
                .build();
            log.info("GET building SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("GET building ERROR: ", e);
            return ApiResponse.<BuildingDTO>builder()
                .code(400)
                .message("Lấy thông tin tòa nhà thất bại")
                .result(null)
                .build();
        }

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BuildingDTO> createBuilding(@RequestPart(name = "data") BuildingCreateRequest buildingPersistRequest,
                                                      @RequestPart(name = "images", required = false) MultipartFile[] images) {
        log.info("POST create building START");
        try {
            ApiResponse<BuildingDTO> response = ApiResponse.<BuildingDTO>builder()
                .code(201)
                .message("Tạo tòa nhà thành công")
                .result(buildingService.createBuilding(buildingPersistRequest, images))
                .build();
            log.info("POST create building SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("POST create building ERROR: ", e);
            return ApiResponse.<BuildingDTO>builder()
                .code(400)
                .message("Tạo tòa nhà thất bại")
                .result(null)
                .build();
        }
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BuildingDTO> updateBuilding(@RequestPart(name = "data") BuildingUpdateRequest buildingUpdateRequest,
                                                      @RequestPart(name = "newImages", required = false) MultipartFile[] newImages) {
        log.info("PUT update building START");
        try {
            ApiResponse<BuildingDTO> response = ApiResponse.<BuildingDTO>builder()
                .code(200)
                .message("Cập nhật tòa nhà thành công")
                .result(buildingService.updateBuilding(buildingUpdateRequest, newImages))
                .build();
            log.info("PUT update building SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("PUT update building ERROR: ", e);
            return ApiResponse.<BuildingDTO>builder()
                .code(400)
                .message("Cập nhật tòa nhà thất bại ")
                .result(null)
                .build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBuilding(@PathVariable Long id) {
        log.info("DELETE building START");
        try {
            buildingService.deleteBuilding(id);
            log.info("DELETE building SUCCESS");
            return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa tòa nhà thành công")
                .result(null)
                .build();
        } catch (Exception e) {
            log.error("DELETE building ERROR: ", e);
            return ApiResponse.<Void>builder()
                .code(400)
                .message("Xóa tòa nhà thất bại")
                .result(null)
                .build();
        }
    }
}
