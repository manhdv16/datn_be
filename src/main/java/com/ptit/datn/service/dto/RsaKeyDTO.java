package com.ptit.datn.service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsaKeyDTO {
    private String publicKey;
    private String privateKey;
}
