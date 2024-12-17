package com.ptit.datn.service.dto;

import com.ptit.datn.config.Constants;
import com.ptit.datn.domain.Authority;
import com.ptit.datn.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
public class AdminUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "INVALID_USERNAME")
    @Pattern(regexp = Constants.LOGIN_REGEX, message = "INVALID_USERNAME")
    @Size(min = 1, max = 50, message = "INVALID_USERNAME")
    private String login;

    @Size(max = 100)
    @NotBlank(message = "INVALID_FULLNAME")
    private String fullName;

    @Size(max = 15)
    @NotBlank(message = "INVALID_PHONE_NUMBER")
    private String phoneNumber;

    @Email
    @Size(min = 5, max = 254)
    @NotBlank(message = "INVALID_EMAIL")
    private String email;

    private MultipartFile imageDigitalSignature;

    private MultipartFile imageAvatar;

    private boolean activated = false;

//    @Size(min = 2, max = 10)
    private String langKey;

    @NotBlank(message = "INVALID_CCCD")
    private String cccd;

    private String address;

    private LocalDate dob;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<String> authorities;

    public AdminUserDTO() {
        // Empty constructor needed for Jackson.
    }

    public AdminUserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.cccd = user.getCccd();
        this.address = user.getAddress();
        this.dob = user.getDob();
        this.activated = user.isActivated();
        this.imageDigitalSignature = null;
        this.imageAvatar = null;
        this.langKey = user.getLangKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AdminUserDTO{" +
            "login='" + login + '\'' +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", imageUrl='" + imageDigitalSignature + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            ", authorities=" + authorities +
            "}";
    }
}
