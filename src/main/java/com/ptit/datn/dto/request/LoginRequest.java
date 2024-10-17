package com.ptit.datn.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotBlank(message = "USERNAME_IS_NOT_BLANK")
    String username;

    @NotBlank(message = "PASSWORD_IS_NOT_BLANK")
    String password;
}
