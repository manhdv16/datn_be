package com.ptit.datn.dto.request;

import com.ptit.datn.constants.Constants;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Pattern(regexp = Constants.LOGIN_REGEX, message = "INVALID_USERNAME")
    String username;

    @Pattern(regexp = Constants.PASSWORD_REGEX, message = "INVALID_PASSWORD")
    String password;
}
