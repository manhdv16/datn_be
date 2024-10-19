package com.ptit.datn.dto.request;

import com.ptit.datn.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

    @NotBlank(message = "INVALID_OLD_PASSWORD")
    String oldPassword;

    @NotBlank(message = "NEW_PASSWORD_IS_NOT_BLANK")
    @Pattern(regexp = Constants.PASSWORD_REGEX, message = "INVALID_PASSWORD")
    String newPassword;

    @NotBlank(message = "NEW_CONFIRM_PASSWORD_IS_NOT_BLANK")
    String confirmPassword;
}
