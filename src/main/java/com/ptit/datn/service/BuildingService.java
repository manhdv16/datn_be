package com.ptit.datn.service;

import com.ptit.datn.domain.Building;
import com.ptit.datn.repository.BuildingRepository;
import com.ptit.datn.repository.WardRepository;
import com.ptit.datn.service.dto.BuildingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BuildingService {

    private static final Logger log = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final WardRepository wardRepository;

    public BuildingService(BuildingRepository buildingRepository, WardRepository wardRepository) {
        this.buildingRepository = buildingRepository;
        this.wardRepository = wardRepository;
    }

    @Transactional(readOnly = true)
    public Page<BuildingDTO> getBuildings(Pageable pageable, String search) {
        log.info("Get buildings");
        return buildingRepository.findAll(pageable).map(BuildingDTO::new);
    }

    @Transactional(readOnly = true)
    public BuildingDTO getBuildingById(Long id) {
        log.info("Get building");
        return buildingRepository.findById(id).map(BuildingDTO::new).orElse(null);
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
        building.setNote(buildingDTO.getNote());
        return new BuildingDTO(buildingRepository.save(building));
    }

    public void deleteBuilding(Long id) {
        log.info("Delete building");
        buildingRepository.deleteById(id);
    }
}
