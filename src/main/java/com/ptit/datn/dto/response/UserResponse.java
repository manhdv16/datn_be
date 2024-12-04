package com.ptit.datn.dto.response;

import com.ptit.datn.domain.Authority;
import com.ptit.datn.domain.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String login;
    String fullName;
    String phoneNumber;
    String email;
    String signImage;
    String langKey;
    String cccd;
    String address;
    LocalDate dob;
    Set<String> authorities;

    public UserResponse(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.cccd = user.getCccd();
        this.address = user.getAddress();
        this.dob = user.getDob();
        this.signImage = user.getSignImage();
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }
}
