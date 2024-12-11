package com.ptit.datn.service;

import com.ptit.datn.constants.OfficeStatus;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Office;
import com.ptit.datn.domain.Request;
import com.ptit.datn.domain.User;
import com.ptit.datn.repository.*;
import com.ptit.datn.repository.specification.OfficeSpecification;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.dto.OfficeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class OfficeService {

    private static final Logger log = LoggerFactory.getLogger(OfficeService.class);

    private final OfficeRepository officeRepository;
    private final BuildingRepository buildingRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserBuildingRepository userBuildingRepository;

    public OfficeService(OfficeRepository officeRepository,
                         BuildingRepository buildingRepository,
                         RequestRepository requestRepository,
                         UserRepository userRepository,
                         UserBuildingRepository userBuildingRepository) {
        this.officeRepository = officeRepository;
        this.buildingRepository = buildingRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.userBuildingRepository = userBuildingRepository;
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
    public Page<OfficeDTO> getOfficesForManage(Pageable pageable,
                                               String search,
                                               Long buildingId,
                                               Long wardId,
                                               Long districtId,
                                               Long provinceId,
                                               BigInteger minPrice,
                                               BigInteger maxPrice,
                                               Double minArea,
                                               Double maxArea,
                                               Integer status) {
        log.info("Get offices for manage");

        // Get current user
        User user = userRepository.findById(Long.valueOf(SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AuthorizationDeniedException("Không tìm thấy người dùng hiện tại",
                    new AuthorizationDecision(false)))))
            .orElseThrow(() -> new AuthorizationDeniedException("Không tìm thấy người dùng hiện tại",
                new AuthorizationDecision(false)));

        // Check if user is manager or admin
        boolean isManager = user.getAuthorities().stream().anyMatch(role -> role.getName().equals("ROLE_MANAGER"));
        boolean isAdmin = user.getAuthorities().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!isManager && !isAdmin) {
            throw new AuthorizationDeniedException("Bạn không có quyền truy cập", new AuthorizationDecision(false));
        }

        // Get all building ids that the user is manager
        Set<Long> buildingIds = userBuildingRepository.findByUserId(user.getId()).stream()
            .map(userBuilding -> userBuilding.getBuildingId())
            .collect(Collectors.toSet());
        if (buildingIds.isEmpty() && !isAdmin) {
            return Page.empty();
        }

        Specification<Office> spec = Specification.where(null);
        if (search != null)
            spec = spec.and(OfficeSpecification.search(search));
        if (buildingId != null) {
            if (!buildingIds.contains(buildingId) && !isAdmin) {
                return Page.empty();
            }
            spec = spec.and(OfficeSpecification.hasBuildingId(buildingId));
        } else if (!isAdmin) {
            spec = spec.and(OfficeSpecification.hasBuildingIdIn(buildingIds));
        }
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
