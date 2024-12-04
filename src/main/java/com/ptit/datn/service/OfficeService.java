package com.ptit.datn.service;

import com.ptit.datn.constants.OfficeStatus;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Office;
import com.ptit.datn.domain.Request;
import com.ptit.datn.repository.BuildingRepository;
import com.ptit.datn.repository.OfficeRepository;
import com.ptit.datn.repository.RequestRepository;
import com.ptit.datn.repository.specification.OfficeSpecification;
import com.ptit.datn.service.dto.OfficeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Set;

@Service
@Transactional
public class OfficeService {

    private static final Logger log = LoggerFactory.getLogger(OfficeService.class);

    private final OfficeRepository officeRepository;
    private final BuildingRepository buildingRepository;
    private final RequestRepository requestRepository;

    public OfficeService(OfficeRepository officeRepository,
                         BuildingRepository buildingRepository,
                         RequestRepository requestRepository) {
        this.officeRepository = officeRepository;
        this.buildingRepository = buildingRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public Page<OfficeDTO> getOffices(Pageable pageable, String search, Long buildingId, Long wardId, Long districtId,
                                      Long provinceId, BigInteger minPrice, BigInteger maxPrice,
                                      Double minArea, Double maxArea, Integer status) {
        log.info("Get offices");
        Specification<Office> spec = Specification.where(null);
        if (search != null)
            spec = spec.and(OfficeSpecification.search(search));
        if (buildingId != null)
            spec = spec.and(OfficeSpecification.hasBuildingId(buildingId));
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
        if (status != null)
            spec = spec.and(OfficeSpecification.hasStatus(status));
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
        office.setName(officeDTO.getName());
        office.setArea(officeDTO.getArea());
        office.setFloor(officeDTO.getFloor());
        office.setPrice(officeDTO.getPrice());
        Building building = buildingRepository.findById(officeDTO.getBuildingId()).orElseThrow(() ->
            new EntityNotFoundException("Building not found with id " + officeDTO.getBuildingId()));
        if (building.getNumberOfFloor() < office.getFloor()) {
            throw new IllegalArgumentException("Floor number is greater than number of floors in building");
        }
        office.setBuilding(building);
        office.setStatus(OfficeStatus.AVAILABLE);
        office.setNote(officeDTO.getNote());
        return new OfficeDTO(officeRepository.save(office));
    }

    public OfficeDTO updateOffice(OfficeDTO officeDTO) {
        log.info("Update office");
        Office office = officeRepository.findById(officeDTO.getId()).orElseThrow();
        office.setName(officeDTO.getName());
        office.setArea(officeDTO.getArea());
        office.setFloor(officeDTO.getFloor());
        office.setPrice(officeDTO.getPrice());
        office.setStatus(officeDTO.getStatus());
        office.setBuilding(buildingRepository.findById(officeDTO.getBuildingId()).orElseThrow());
        office.setNote(officeDTO.getNote());
        return new OfficeDTO(officeRepository.save(office));
    }

    public void deleteOffice(Long id) {
        log.info("Delete office");
        Office office = officeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Office not found with id " + id));

        // Lấy các request liên kết với office trước khi xóa
        Set<Request> associatedRequests = office.getRequests();

        // Xóa office
        officeRepository.delete(office);

        // Kiểm tra và xóa các request không còn liên kết với office nào
        for (Request request : associatedRequests) {
            if (request.getOffices().isEmpty()) {
                requestRepository.delete(request);
            }
        }
    }
}
