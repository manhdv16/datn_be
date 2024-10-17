package com.ptit.datn.dto.request;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientLoginRequest {

    @NotBlank(message = "TAXCODE_IS_NOT_BLANK")
    String taxCode;

    @NotBlank(message = "PASSWORD_IS_NOT_BLANK")
    String adminPassword;

    @NotNull(message = "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED")
    Integer digitalSignatureType;

    @NotBlank(message = "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED")
    String digitalSignature;

    @NotBlank(message = "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED")
    String serial;

    @NotBlank(message = "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED")
    String provider;

    @NotNull(message = "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED")
    Date effectiveDate;

    @NotNull(message = "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED")
    Date expiryDate;

    @NotBlank(message = "FIELDS_OF_DIGITAL_SIGNATURE_ARE_REQUIRED")
    String publicKey;
}
