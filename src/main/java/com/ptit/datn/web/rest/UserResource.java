package com.ptit.datn.web.rest;

import com.ptit.datn.domain.User;
import com.ptit.datn.dto.request.UserListRequest;
import com.ptit.datn.dto.response.ApiResponse;
import com.ptit.datn.dto.response.UserResponse;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.UserRepository;
import com.ptit.datn.security.AuthoritiesConstants;
import com.ptit.datn.service.MailService;
import com.ptit.datn.service.UserService;
import com.ptit.datn.service.dto.AdminUserDTO;
import com.ptit.datn.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/admin")
public class UserResource {

    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ApiResponse<User> createUser(@ModelAttribute @Valid AdminUserDTO userDTO) throws Exception {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new AppException(ErrorCode.USER_ID_EXISTED);
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        } else {
            User newUser = userService.createUser(userDTO);
            mailService.sendCreationEmail(newUser);
            return ApiResponse.<User>builder().message("User created").result(newUser).build();
        }
    }

    @PutMapping({ "/users/{id}" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") Long id, @ModelAttribute @Valid AdminUserDTO userDTO) throws Exception {
        log.debug("REST request to update User : {}", userDTO);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(id))) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(id))) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        userDTO.setId(id);
        Optional<UserResponse> updatedUser = userService.updateUser(userDTO);
        return ApiResponse.<UserResponse>builder().message("User updated").result(updatedUser.orElse(null)).build();
    }

    @PostMapping("/assign-users-responsible-by-building-id/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ApiResponse<String> assignUserResponsibilityBuilding(@PathVariable("id") Long buildingId, @RequestBody @Valid UserListRequest request) {
        return ApiResponse.<String>builder()
            .result(userService.assignResponsible(buildingId, request))
            .build();
    }

    @GetMapping("/managers")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Page<UserResponse> getAllManagers(Pageable pageable) {
        log.debug("REST request to get all managers for an admin");
        return userService.getAllManagers(pageable);
    }

    @GetMapping("/manager/by-building/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<UserResponse>> getManagerByBuilding(@PathVariable("id") Long id) {
        List<UserResponse> list = userService.getManagerByBuilding(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("/remove-assigned-manager/{buildingId}/{userId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ApiResponse<Void> removeAssignedManager(@PathVariable("buildingId") Long buildingId, @PathVariable("userId") Long userId) {
        log.debug("REST request to delete assigned of manager: {}", buildingId);
        userService.removeAssignedManager(buildingId, userId);
        return ApiResponse.<Void>builder().message("Assigned manager deleted").build();
    }

    @GetMapping("/get-all-managers-not-assigned/{buildingId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<UserResponse>> getAllManagerNotAssignedBuildingId(Pageable pageable, @PathVariable("buildingId") Long buildingId) {
        log.debug("REST request to get all managers not assigned building by {} for an admin", buildingId);

        final Page<UserResponse> page = userService.getAllManagerNotAssignedBuildingId(pageable, buildingId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }



    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<UserResponse>> getAllUsers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get all User for an admin");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserResponse> page = userService.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        log.debug("REST request to get User : {}", id);
        return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesById(id).map(UserResponse::new));
    }

    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.MANAGER + "\")")
    public ResponseEntity<UserResponse> getCustomer(@PathVariable("id") Long id) {
        log.debug("REST request to get User : {}", id);
        return ResponseUtil.wrapOrNotFound(userService.getCustomerById(id).map(UserResponse::new));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ApiResponse<Void> deleteUser(@PathVariable("id") Long id) {
        log.debug("REST request to delete User: {}", id);
        userService.deleteUser(id);
        return ApiResponse.<Void>builder().message("User deleted").build();
    }



    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList(
            "id",
            "login",
            "firstName",
            "lastName",
            "email",
            "activated",
            "langKey",
            "createdBy",
            "createdDate",
            "lastModifiedBy",
            "lastModifiedDate"
        )
    );

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final MailService mailService;

    public UserResource(UserService userService, UserRepository userRepository, MailService mailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }
}
