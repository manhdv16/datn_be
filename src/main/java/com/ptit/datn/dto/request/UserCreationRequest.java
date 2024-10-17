package com.ptit.datn.dto.request;

import com.vn.customs.gov.auth.constants.Constants;
import javax.validation.constraints.Pattern;
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
