package com.ptit.datn.service;

import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Office;
import com.ptit.datn.repository.BuildingRepository;
import com.ptit.datn.repository.OfficeRepository;
import com.ptit.datn.repository.specification.OfficeSpecification;
import com.ptit.datn.service.dto.OfficeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional
public class OfficeService {

    private static final Logger log = LoggerFactory.getLogger(OfficeService.class);

    private final OfficeRepository officeRepository;
    private final BuildingRepository buildingRepository;

    public OfficeService(OfficeRepository officeRepository,
                         BuildingRepository buildingRepository) {
        this.officeRepository = officeRepository;
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
    public Page<OfficeDTO> getOffices(Pageable pageable, String search, Long wardId, Long districtId,
                                      Long provinceId, BigInteger minPrice, BigInteger maxPrice,
                                      Double minArea, Double maxArea) {
        log.info("Get offices");
        Specification<Office> spec = Specification.where(null);
        if (search != null)
            spec = spec.and(OfficeSpecification.search(search));
        if (wardId != null)
            spec = spec.and(OfficeSpecification.hasWardId(wardId));
        if (districtId != null)
            spec = spec.and(OfficeSpecification.hasDistrictId(districtId));
        if (provinceId != null)
            spec = spec.and(OfficeSpecification.hasProvinceId(provinceId));
        if (minPrice != null)
            spec = spec.and(OfficeSpecification.hasPriceGreaterOrEqual(minPrice));
        if (maxPrice != null)
            spec = spec.and(OfficeSpecification.hasPriceLessOrEqual(maxPrice));
        if (minArea != null)
            spec = spec.and(OfficeSpecification.hasAreaGreaterOrEqual(minArea));
        if (maxArea != null)
            spec = spec.and(OfficeSpecification.hasAreaLessOrEqual(maxArea));
        Page<Office> offices = officeRepository.findAll(spec, pageable);
        return offices.map(OfficeDTO::new);
    }

    @Transactional(readOnly = true)
    public OfficeDTO getOffice(Long id) {
        log.info("Get office");
        return officeRepository.findById(id).map(OfficeDTO::new).orElseThrow();
    }

    public OfficeDTO createOffice(OfficeDTO officeDTO) {
        log.info("Create office");
        Office office = new Office();
        office.setArea(officeDTO.getArea());
        office.setFloor(officeDTO.getFloor());
        office.setPrice(officeDTO.getPrice());
        office.setBuilding(buildingRepository.findById(officeDTO.getBuildingId()).orElseThrow());
        office.setNote(officeDTO.getNote());
        return new OfficeDTO(officeRepository.save(office));
    }

    public OfficeDTO updateOffice(OfficeDTO officeDTO) {
        log.info("Update office");
        Office office = officeRepository.findById(officeDTO.getId()).orElseThrow();
        office.setArea(officeDTO.getArea());
        office.setFloor(officeDTO.getFloor());
        office.setPrice(officeDTO.getPrice());
        office.setBuilding(buildingRepository.findById(officeDTO.getBuildingId()).orElseThrow());
        office.setNote(officeDTO.getNote());
        return new OfficeDTO(officeRepository.save(office));
    }

    public void deleteOffice(Long id) {
        log.info("Delete office");
        officeRepository.deleteById(id);
    }
}
