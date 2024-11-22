package com.ptit.datn.web.rest;

import com.ptit.datn.domain.User;
import com.ptit.datn.dto.request.ForgotPasswordRequest;
import com.ptit.datn.dto.response.ApiResponse;
import com.ptit.datn.dto.response.UserResponse;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import com.ptit.datn.repository.UserRepository;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.MailService;
import com.ptit.datn.service.UserService;
import com.ptit.datn.service.dto.AdminUserDTO;
import com.ptit.datn.service.dto.PasswordChangeDTO;
import com.ptit.datn.web.rest.errors.*;
import com.ptit.datn.web.rest.vm.KeyAndPasswordVM;
import com.ptit.datn.web.rest.vm.ManagedUserVM;
import jakarta.validation.Valid;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) throws Exception {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        //        mailService.sendActivationEmail(user);
        return ApiResponse.builder().message("User registered").build();
    }

    @GetMapping("/account")
    public ApiResponse<UserResponse> getAccount() {
        UserResponse userResponse= userService
            .getUserWithAuthorities()
            .map(UserResponse::new)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return ApiResponse.<UserResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Get account successfully")
            .result(userResponse)
            .build();
    }

    @PostMapping(path = "/account/change-password")
    public ApiResponse changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
        return ApiResponse.builder().message("Password changed").build();
    }

    @PostMapping(path = "/account/reset-password/init")
    public ApiResponse requestPasswordReset(@RequestBody @Valid ForgotPasswordRequest request) {
        Optional<User> user = userService.requestPasswordReset(request.getMail());
        mailService.sendPasswordResetMail(user.orElseThrow());
        return ApiResponse.builder().message("The key to reset your password has been sent to your email.").build();
    }

    @PostMapping(path = "/account/reset-password/finish")
    public ApiResponse finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
        return ApiResponse.builder().message("Password reset successfully").build();
    }

    @PutMapping("/account/update")
    public ApiResponse<AdminUserDTO> saveAccount(@Valid @RequestBody AdminUserDTO userDTO) throws Exception {
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        userDTO.setId(null);
        Optional<AdminUserDTO> updatedUser = userService.updateUser(userDTO);
        return ApiResponse.<AdminUserDTO>builder().message("User updated").result(updatedUser.orElse(null)).build();
    }


    // not use
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
