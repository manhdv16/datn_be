package com.ptit.datn.service;

import com.ptit.datn.constants.RequestStatus;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Office;
import com.ptit.datn.domain.Request;
import com.ptit.datn.domain.User;
import com.ptit.datn.repository.*;
import com.ptit.datn.security.AuthoritiesConstants;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.dto.BuildingDTO;
import com.ptit.datn.service.dto.OfficeDTO;
import com.ptit.datn.service.dto.RequestDTO;
import com.ptit.datn.service.dto.UserDTO;
import jakarta.persistence.RollbackException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;
    private final OfficeRepository officeRepository;
    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final UserBuildingRepository userBuildingRepository;

    public RequestService(RequestRepository requestRepository,
                          OfficeRepository officeRepository,
                          UserRepository userRepository,
                          BuildingRepository buildingRepository,
                          UserBuildingRepository userBuildingRepository) {
        this.requestRepository = requestRepository;
        this.officeRepository = officeRepository;
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.userBuildingRepository = userBuildingRepository;
    }

    /**
     *
     * @param requestDTO RequestDTO object that contains information of the request
     * @return RequestDTO
     */
    public RequestDTO createRequest(RequestDTO requestDTO) {
        Request request = new Request();
        request.setBuildingId(requestDTO.getBuildingId());
        request.setDate(requestDTO.getDate());
        request.setTime(requestDTO.getTime());
        request.setNote(requestDTO.getNote());
        request.setStatus(RequestStatus.PENDING); // When user create a request, the status is PENDING

        // Get the author of the request
        User author = userRepository.findById(
                Long.valueOf(SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("Thông tin người gửi không hợp lệ"))))
            .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        boolean isManager = author.getAuthorities().stream().anyMatch(role -> role.getName().equals(AuthoritiesConstants.MANAGER))
            || author.getAuthorities().stream().anyMatch(role -> role.getName().equals(AuthoritiesConstants.ADMIN));

        List<Office> officeList = officeRepository.findAllByIds(requestDTO.getOfficeIds());
        if (officeList.size() != requestDTO.getOfficeIds().size()) {
            throw new RollbackException("Văn phòng không tồn tại");
        }

        officeList.forEach(office -> {
            if (office.getStatus() != 0)
                throw new RollbackException("Tồn tại văn phòng không thể thuê");

            request.getOffices().add(office);
        });

        // If the author is a manager or an admin
        if(isManager) {
            request.setManagerId(author.getId());
            request.setStatus(RequestStatus.ACCEPTED);
        }
        // If the author is a user
        else {
            request.setUserId(author.getId());
            request.setStatus(RequestStatus.PENDING);
            if(requestDTO.getManagerId() != null) {
                request.setManagerId(requestDTO.getManagerId());
            }
        }

        RequestDTO requestDTO_result = new RequestDTO(requestRepository.save(request));
        requestDTO_result.setOfficeIds(requestDTO.getOfficeIds());

        return requestDTO_result;
    }

    @Transactional(readOnly = true)
    public Page<RequestDTO> getAllRequests(Pageable pageable, Integer status, Long userId) {

        Specification<Request> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (userId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("userId"), userId));
            }
            return predicate;
        };

        Page<Request> requests = requestRepository.findAll(spec, pageable);
        Page<RequestDTO> requestDTOS = requests.map(RequestDTO::new);

        // Get userDTOs
        requestDTOS.forEach(requestDTO -> {
            if (requestDTO.getUserId() != null) {
                userRepository.findById(requestDTO.getUserId()).ifPresent(user -> requestDTO.setUserDTO(new UserDTO(user)));
            }
            if (requestDTO.getBuildingId() != null) {
                buildingRepository.findById(requestDTO.getBuildingId()).ifPresent(building -> requestDTO.setBuildingDTO(new BuildingDTO(building)));
            }
        });

        return requestDTOS;
    }

    @Transactional(readOnly = true)
    public Page<RequestDTO> getAllRequestsForManage(Pageable pageable, Integer status) {

        // Get current user
        User user = userRepository.findById(Long.valueOf(SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException("Thông tin người dùng không hợp lệ")))
        ).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Check if user is manager or admin
        boolean isManager = user.getAuthorities().stream().anyMatch(role -> role.getName().equals(AuthoritiesConstants.MANAGER));
        boolean isAdmin = user.getAuthorities().stream().anyMatch(role -> role.getName().equals(AuthoritiesConstants.ADMIN));
        if (!isManager && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền truy cập");
        }

        // Get all building ids that the user is manager
        Set<Long> buildingIds = userBuildingRepository.findByUserId(user.getId()).stream()
            .map(userBuilding -> userBuilding.getBuildingId())
            .collect(Collectors.toSet());
        if (buildingIds.isEmpty() && !isAdmin) {
            return Page.empty();
        }

        Specification<Request> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (!isAdmin) {
                predicate = criteriaBuilder.and(predicate, root.get("buildingId").in(buildingIds));
            }
            return predicate;
        };

        Page<Request> requests = requestRepository.findAll(spec, pageable);
        Page<RequestDTO> requestDTOS = requests.map(RequestDTO::new);

        // Get userDTOs
        requestDTOS.forEach(requestDTO -> {
            if (requestDTO.getUserId() != null) {
                userRepository.findById(requestDTO.getUserId())
                    .ifPresent(u -> requestDTO.setUserDTO(new UserDTO(u)));
            }
            if (requestDTO.getBuildingId() != null) {
                buildingRepository.findById(requestDTO.getBuildingId())
                    .ifPresent(b -> requestDTO.setBuildingDTO(new BuildingDTO(b)));
            }
        });

        return requestDTOS;
    }

    public Page<RequestDTO> getAllRequestsByUser(Pageable pageable, Integer status) {
        User user = userRepository.findById(Long.valueOf(SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException("Thông tin người dùng không hợp lệ")))
        ).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Specification<Request> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("userId"), user.getId()));
            return predicate;
        };

        Page<Request> requests = requestRepository.findAll(spec, pageable);
        Page<RequestDTO> requestDTOS = requests.map(RequestDTO::new);

        requestDTOS.forEach(requestDTO -> {
            if (requestDTO.getBuildingId() != null) {
                buildingRepository.findById(requestDTO.getBuildingId())
                    .ifPresent(b -> requestDTO.setBuildingDTO(new BuildingDTO(b)));
            }
        });

        return requestDTOS;
    }

    @Transactional(readOnly = true)
    public RequestDTO getRequestById(Long id) {
        Request request = requestRepository.findById(id).orElseThrow();
        RequestDTO requestDTO = new RequestDTO(request);

        // Get userDTO
        if (request.getUserId() != null) {
            userRepository.findById(request.getUserId()).ifPresent(user -> requestDTO.setUserDTO(new UserDTO(user)));
        }

        // Get buildingDTO
        requestDTO.setBuildingDTO(new BuildingDTO(buildingRepository.findById(request.getBuildingId()).orElseThrow()));

        // Get officeDTOs
        Set<Office> offices = request.getOffices();
        Set<OfficeDTO> officeDTOs = new HashSet<>();
        offices.forEach(office -> {
            OfficeDTO officeDTO = new OfficeDTO(office);
            officeDTOs.add(officeDTO);
        });
        requestDTO.setOfficeDTOs(officeDTOs);

        return requestDTO;
    }

    public RequestDTO updateRequest(RequestDTO requestDTO) {
        Request request = requestRepository.findById(requestDTO.getId()).orElseThrow();
        request.setUserId(requestDTO.getUserId());
        // request.setOfficeIds(requestDTO.getOfficeIds());
        request.setDate(requestDTO.getDate());
        request.setTime(requestDTO.getTime());
        request.setNote(requestDTO.getNote());
        request.setStatus(requestDTO.getStatus());
        return new RequestDTO(requestRepository.save(request));
    }
}
