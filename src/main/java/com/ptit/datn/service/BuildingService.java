package com.ptit.datn.service;

import com.ptit.datn.domain.Building;
import com.ptit.datn.repository.BuildingRepository;
import com.ptit.datn.repository.OfficeRepository;
import com.ptit.datn.repository.WardRepository;
import com.ptit.datn.service.dto.BuildingDTO;
import com.ptit.datn.service.dto.OfficeDTO;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingService {

    private static final Logger log = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final OfficeRepository officeRepository;
    private final WardRepository wardRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           WardRepository wardRepository,
                           OfficeRepository officeRepository) {
        this.buildingRepository = buildingRepository;
        this.wardRepository = wardRepository;
        this.officeRepository = officeRepository;
    }

    @Transactional(readOnly = true)
    public Page<BuildingDTO> getBuildings(Pageable pageable,
                                          String keyword,
                                          Long wardId,
                                          Long districtId,
                                          Long provinceId,
                                          BigInteger minPrice,
                                          BigInteger maxPrice,
                                          Double minArea,
                                          Double maxArea) {
        log.info("Get buildings");

        Specification<Building> spec = (root, query, cb) -> {
            // Join with the offices table to filter by price/area
            Join<Object, Object> officeJoin = root.join("offices", JoinType.LEFT);

            Predicate predicate = cb.conjunction(); // Base condition

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), pattern);
                Predicate addressPredicate = cb.like(cb.lower(root.get("address")), pattern);
                predicate = cb.and(predicate, cb.or(namePredicate, addressPredicate));
            }

            if (wardId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("ward").get("id"), wardId));
            } else if (districtId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("ward").get("district").get("id"), districtId));
            } else if (provinceId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("ward").get("district").get("province").get("id"), provinceId));
            }

            if (minPrice != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(officeJoin.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(officeJoin.get("price"), maxPrice));
            }
            if (minArea != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(officeJoin.get("area"), minArea));
            }
            if (maxArea != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(officeJoin.get("area"), maxArea));
            }
            return predicate;
        };

        Page<Building> buildings = buildingRepository.findAll(spec, pageable);
        return buildings.map(BuildingDTO::new);

    }

    @Transactional(readOnly = true)
    public BuildingDTO getBuildingById(Long id) {
        log.info("Get building");
        Optional<Building> buildingOptional = buildingRepository.findById(id);
        if (buildingOptional.isEmpty()) {
            return null;
        }
        BuildingDTO buildingDTO = new BuildingDTO(buildingOptional.orElseThrow());
        buildingDTO.setOfficeDTOS(officeRepository.findAllByBuildingId(id).stream().map(OfficeDTO::new).collect(Collectors.toList()));
        return buildingDTO;
    }

    public BuildingDTO createBuilding(BuildingDTO buildingDTO) {
        log.info("Create building");
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setWard(wardRepository.findById(buildingDTO.getWardId()).orElseThrow());
        building.setNumberOfFloor(buildingDTO.getNumberOfFloor());
        building.setNumberOfBasement(buildingDTO.getNumberOfBasement());
        building.setPricePerM2(buildingDTO.getPricePerM2());
        building.setFloorHeight(buildingDTO.getFloorHeight());
        building.setFloorArea(buildingDTO.getFloorArea());
        building.setFacilities(buildingDTO.getFacilities());
        building.setCarParkingFee(buildingDTO.getCarParkingFee());
        building.setMotorbikeParkingFee(buildingDTO.getMotorbikeParkingFee());
        building.setSecurityFee(buildingDTO.getSecurityFee());
        building.setCleaningFee(buildingDTO.getCleaningFee());
        building.setNote(buildingDTO.getNote());
        return new BuildingDTO(buildingRepository.save(building));
    }

    public BuildingDTO updateBuilding(BuildingDTO buildingDTO) {
        log.info("Update building");
        Building building = buildingRepository.findById(buildingDTO.getId()).orElseThrow();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setWard(wardRepository.findById(buildingDTO.getWardId()).orElseThrow());
        building.setNumberOfFloor(buildingDTO.getNumberOfFloor());
        building.setNumberOfBasement(buildingDTO.getNumberOfBasement());
        building.setPricePerM2(buildingDTO.getPricePerM2());
        building.setFloorHeight(buildingDTO.getFloorHeight());
        building.setFloorArea(buildingDTO.getFloorArea());
        building.setFacilities(buildingDTO.getFacilities());
        building.setCarParkingFee(buildingDTO.getCarParkingFee());
        building.setMotorbikeParkingFee(buildingDTO.getMotorbikeParkingFee());
        building.setSecurityFee(buildingDTO.getSecurityFee());
        building.setCleaningFee(buildingDTO.getCleaningFee());
        building.setNote(buildingDTO.getNote());
        return new BuildingDTO(buildingRepository.save(building));
    }

    public void deleteBuilding(Long id) {
        log.info("Delete building");
        buildingRepository.deleteById(id);
    }
}
