package com.ptit.datn.service;

import com.ptit.datn.constants.Constants;
import com.ptit.datn.constants.RequestStatus;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Office;
import com.ptit.datn.domain.Request;
import com.ptit.datn.domain.User;
import com.ptit.datn.dto.request.StatusAcceptRequest;
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
    private final NotificationService notificationService;

    public RequestService(RequestRepository requestRepository,
                          OfficeRepository officeRepository,
                          UserRepository userRepository,
                          BuildingRepository buildingRepository,
                          UserBuildingRepository userBuildingRepository,
                          NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.officeRepository = officeRepository;
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.userBuildingRepository = userBuildingRepository;
        this.notificationService = notificationService;
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

        // Send notification to manager
        if (requestDTO.getManagerId() != null) {
            notificationService.notifyUser(requestDTO.getManagerId(), Constants.TOPIC.REQUEST,
                 "99|" + requestDTO_result.getId());
        }

        // Send notification to user
        notificationService.notifyUser(requestDTO.getUserId(), Constants.TOPIC.REQUEST,
            RequestStatus.PENDING + "|" + requestDTO_result.getId());

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
                userRepository.findById(requestDTO.getUserId())
                    .ifPresent(user -> requestDTO.setUserDTO(new UserDTO(user)));
            }
            if (requestDTO.getBuildingId() != null) {
                buildingRepository.findById(requestDTO.getBuildingId())
                    .ifPresent(building -> requestDTO.setBuildingDTO(new BuildingDTO(building)));
            }
        });

        return requestDTOS;
    }

    @Transactional(readOnly = true)
    public Page<RequestDTO> getAllRequestsForManage(Pageable pageable, Integer status, Long buildingId) {

        // Get current user
        Long authorId = Long.valueOf(SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException("Thông tin người dùng không hợp lệ")));
        User user = userRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

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
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("managerId")),
                    criteriaBuilder.equal(root.get("managerId"), authorId)));
            }
            if (buildingId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("buildingId"), buildingId));
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

    //region Handle request
    public RequestDTO handleAccept(Long id, StatusAcceptRequest req) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setDate(req.getDate());
        request.setTime(req.getTime());
        request.setStatus(RequestStatus.ACCEPTED);
        request.setManagerId(Long.valueOf(SecurityUtils.getCurrentUserLogin().orElseThrow()));

        // Send notification to user
        notificationService.notifyUser(request.getUserId(), Constants.TOPIC.REQUEST,
            RequestStatus.ACCEPTED + "|" + request.getId());

        return new RequestDTO(requestRepository.save(request));
    }

    public RequestDTO handleReject(Long id) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setStatus(RequestStatus.REJECTED);

        // Send notification to user
        notificationService.notifyUser(request.getUserId(), Constants.TOPIC.REQUEST,
            RequestStatus.REJECTED +"|" + request.getId());

        return new RequestDTO(requestRepository.save(request));
    }

    public RequestDTO handleCancel(Long id) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setStatus(RequestStatus.CANCELED);

        // Send notification to user
        notificationService.notifyUser(request.getUserId(), Constants.TOPIC.REQUEST,
            RequestStatus.CANCELED + "|" + request.getId());

        return new RequestDTO(requestRepository.save(request));
    }

    public RequestDTO handleComplete(Long id) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setStatus(RequestStatus.COMPLETED);

        // Send notification to user
        notificationService.notifyUser(request.getUserId(), Constants.TOPIC.REQUEST,
            RequestStatus.COMPLETED + "|" + request.getId());


        return new RequestDTO(requestRepository.save(request));
    }

    public Void deleteRequest(Long id) {
        try {
            requestRepository.deleteById(id);
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException("Xóa yêu cầu không thành công");
        }
    }
    //endregion
}
