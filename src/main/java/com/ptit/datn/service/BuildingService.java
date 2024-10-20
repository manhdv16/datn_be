package com.ptit.datn.service;

import com.ptit.datn.domain.Building;
import com.ptit.datn.repository.BuildingRepository;
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

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
    public Page<BuildingDTO> getBuildings(Pageable pageable) {
        log.info("Get buildings");
        return buildingRepository.findAll(pageable).map(BuildingDTO::new);
    }

    @Transactional(readOnly = true)
    public BuildingDTO getBuildingById(Long id) {
        log.info("Get building");
        return buildingRepository.findById(id).map(BuildingDTO::new).orElse(null);
    }

    public Building createBuilding(BuildingDTO buildingDTO) {
        log.info("Create building");
        Building building = Building.builder()
                .name(buildingDTO.getName())
                .address(buildingDTO.getAddress())
                .facilities(buildingDTO.getFacilities())
                .note(buildingDTO.getNote())
                .build();
        return buildingRepository.save(building);
    }

    public Building updateBuilding(BuildingDTO buildingDTO) {
        log.info("Update building");
        Building building = buildingRepository.findById(buildingDTO.getId()).orElseThrow();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setFacilities(buildingDTO.getFacilities());
        building.setNote(buildingDTO.getNote());
        return buildingRepository.save(building);
    }

    public void deleteBuilding(Long id) {
        log.info("Delete building");
        buildingRepository.deleteById(id);
    }
}
