package com.ptit.datn.service;

import com.ptit.datn.cloudinary.CloudinaryService;
import com.ptit.datn.config.Constants;
import com.ptit.datn.domain.Authority;
import com.ptit.datn.domain.User;
import com.ptit.datn.domain.UserBuilding;
import com.ptit.datn.dto.request.UserListRequest;
import com.ptit.datn.dto.response.UserResponse;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.AuthorityRepository;
import com.ptit.datn.repository.UserBuildingRepository;
import com.ptit.datn.repository.UserRepository;
import com.ptit.datn.security.AuthoritiesConstants;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.security.jwt.TokenProvider;
import com.ptit.datn.service.dto.AdminUserDTO;
import com.ptit.datn.service.dto.UserDTO;
import com.ptit.datn.service.dto.UserNameDTO;
import com.ptit.datn.utils.DataUtils;
import com.ptit.datn.web.rest.vm.LoginVM;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    static Logger log = LoggerFactory.getLogger(UserService.class);

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    TokenProvider tokenProvider;
    AuthorityRepository authorityRepository;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserBuildingRepository userBuildingRepository;
    CloudinaryService cloudinaryService;
    BuildingService buildingService;
    RedisService redisService;
    NotificationService notificationService;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        User u = userRepository.findOneByResetKey(key).orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        User u = userRepository.findOneByEmailIgnoreCase(mail).orElseThrow(
            ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
            );
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }

    public User registerUser(AdminUserDTO userDTO, String password) throws Exception {
        User user;
        user = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).orElseThrow(
            () -> new AppException(ErrorCode.USER_EXISTED)
        );
        user = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).orElseThrow(
            () -> new AppException(ErrorCode.EMAIL_EXISTED)
        );
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFullName(userDTO.getFullName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        if (!DataUtils.isNullOrEmpty(userDTO.getImageDigitalSignature())) {
            newUser.setDigitalSignature(SignatureService.generateHashFromMultipartFile(userDTO.getImageDigitalSignature()));
        }

        if (!DataUtils.isNullOrEmpty(userDTO.getImageAvatar())) {
            newUser.setImageAvatar((String) cloudinaryService.uploadFile(userDTO.getImageAvatar()).get("url"));
        }
        newUser.setLangKey(userDTO.getLangKey());
        newUser.setFullName(userDTO.getFullName());
        newUser.setCccd(userDTO.getCccd());
        newUser.setAddress(userDTO.getAddress());
        newUser.setDob(userDTO.getDob());
        newUser.setPhoneNumber(userDTO.getPhoneNumber());
        // new user is not active
        newUser.setActivated(true);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public UserNameDTO getUserName(Long id){
        User user = userRepository.findOneById(id).orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return new UserNameDTO(
            user.getId(),
            user.getLogin(),
            user.getFullName(),
            user.getAddress(),
            user.getPhoneNumber(),
            user.getCccd()
        );
    }

    public User createUser(AdminUserDTO userDTO) throws Exception {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        if(!DataUtils.isNullOrEmpty(userDTO.getImageDigitalSignature())) {
            user.setDigitalSignature(SignatureService.generateHashFromMultipartFile(userDTO.getImageDigitalSignature()));
            user.setSignImage((String) cloudinaryService.uploadFile(userDTO.getImageDigitalSignature()).get("url"));
        }
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(Constants.DEFAULT_PASSWORD);
        user.setPassword(encryptedPassword);
        user.setResetKey(Constants.DEFAULT_PASSWORD);
        user.setResetDate(Instant.now());
        user.setActivated(true);
        user.setCccd(userDTO.getCccd());
        user.setAddress(userDTO.getAddress());
        user.setDob(userDTO.getDob());

        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserResponse> updateUser(AdminUserDTO userDTO) throws Exception {
        Long id;
        if (DataUtils.isNullOrEmpty(userDTO.getId())){
            String strId = SecurityUtils.getCurrentUserLogin().orElse(null);
            if (DataUtils.isNullOrEmpty(strId)) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            id = Long.parseLong(strId);
        } else {
            id = userDTO.getId();
        }
        return Optional.of(userRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFullName(userDTO.getFullName());
                user.setPhoneNumber(userDTO.getPhoneNumber());
                user.setEmail(userDTO.getEmail().toLowerCase());
                if(!DataUtils.isNullOrEmpty(userDTO.getImageDigitalSignature())) {
                    try {
                        user.setDigitalSignature(SignatureService
                            .generateHashFromMultipartFile(userDTO.getImageDigitalSignature()));
                        user.setSignImage((String) cloudinaryService
                            .uploadFile(userDTO.getImageDigitalSignature()).get("url"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!DataUtils.isNullOrEmpty(userDTO.getImageAvatar())) {
                    user.setImageAvatar((String) cloudinaryService.uploadFile(userDTO.getImageAvatar()).get("url"));
                }
//                user.setActivated(userDTO.isActivated());
//                user.setLangKey(userDTO.getLangKey());
                if (SecurityUtils.getAuthorities().stream().anyMatch(AuthoritiesConstants.ADMIN::equals)) {
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO
                        .getAuthorities()
                        .stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                }
                userRepository.save(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserResponse::new);
    }

    public void deleteUser(Long id) {
        userRepository
            .findOneById(id)
            .ifPresent(user -> {
                userRepository.delete(user);
                log.debug("Deleted User: {}", user);
            });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        String userId = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (DataUtils.isNullOrEmpty(userId)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        userRepository.findOneById(Long.parseLong(userId)).ifPresent(user -> {
            String currentEncryptedPassword = user.getPassword();
            if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                throw new AppException(ErrorCode.OLD_PASSWORD_NOT_MATCH);
            }
            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            log.debug("Changed password for User: {}", user);
        });
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllManagers(Pageable pageable, String search) {
        return userRepository.findAllByRole(pageable, search).map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getManagerByBuildIdAndPageable(Long buildingId, Pageable pageable) {
        return userRepository.findAllManagerByBuildingId(pageable, buildingId).map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllManagerNotAssignedBuildingId(Pageable pageable, Long buildingId) {
        return userRepository.findAllManagerNotAssignedBuildingId(pageable, buildingId).map(UserResponse::new);
    }


    @Transactional(readOnly = true)
    public List<UserResponse> getManagerByBuilding(Long id) {
        return userRepository.getManagerByBuilding(id).stream().map(UserResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesById(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getCustomerById(Long id) {
        Optional<User> user = userRepository.findOneById(id);
        if (user.orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"))
            .getAuthorities().stream().noneMatch(authority -> authority.getName().equals(AuthoritiesConstants.USER))) {
            throw new RuntimeException("Người dùng không hợp lệ");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        Long id = Long.parseLong(Objects.requireNonNull(SecurityUtils.getCurrentUserLogin().orElse(null)));
        return userRepository.findOneWithAuthoritiesById(id);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
            });
    }

    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).toList();
    }

    public String authenticate(LoginVM request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            );
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Long id = userRepository.getIdByLoginAndActivated(request.getUsername(), true);
            String token = tokenProvider.createToken(id, authentication);
            redisService.save(token, id.toString(), Constants.REDIS_EXPIRE.TOKEN);
            return token;
        } catch (BadCredentialsException e) {
            return null;
        }
    }

    public String assignResponsible(Long buildingId, UserListRequest request) {
        if (DataUtils.isNullOrEmpty(request)) {
            throw new AppException(ErrorCode.EMPTY_REQUEST);
        }
        // check buildId exists
        // check number of user manager in building
        if (request.getListUserId().size() > Constants.MAX_MANAGER) {
            throw new AppException(ErrorCode.NUMBER_MANAGER_OF_BUILDING,Constants.MAX_MANAGER);
        }
        // check number of user manager in building
        Integer numManager = userBuildingRepository.countUserManagerByBuildingId(buildingId);
        if (request.getListUserId().size() > Constants.MAX_MANAGER-numManager) {
            throw new AppException(ErrorCode.NUMBER_MANAGER_OF_BUILDING, Constants.MAX_MANAGER);
        }

        request.getListUserId()
            .forEach(userId -> {
                UserBuilding userBuilding = new UserBuilding();
                userBuilding.setBuildingId(buildingId);
                userBuilding.setUserId(userId);
                userBuildingRepository.save(userBuilding);
                notificationService.notifyUser(userId, com.ptit.datn.constants.Constants.TOPIC.ASSIGN, "Bạn đã được phân công quản lý thêm 1 tòa nhà");
            });
        return "Assign responsible successfully";
    }

    public String getDigitalSignature() {
        Long id = Long.valueOf(SecurityUtils.getCurrentUserLogin().orElseThrow());
        return userRepository.getDigitalSignatureByUserId(id);
    }

    public void removeAssignedManager(Long buildingId, Long userId) {
        Integer count = userBuildingRepository.countUserManagerByBuildingIdAndUserId(buildingId, userId);
        if (count == 0) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userBuildingRepository.deleteByBuildingIdAndUserId(buildingId, userId);
    }

    public List<UserDTO> getAllManagersByBuilding(Long id) {
        return userRepository.findAllManagerByBuildingId(id).stream().map(UserDTO::new).toList();
    }

    public List<UserDTO> getAllByRoleUser() {
        return userRepository.findAllByAuthoritiesName(AuthoritiesConstants.USER).stream().map(UserDTO::new).toList();
    }

    public void logout() {
        String token = SecurityUtils.getCurrentUserLogin().orElseThrow();
        redisService.delete(token);
    }
}
