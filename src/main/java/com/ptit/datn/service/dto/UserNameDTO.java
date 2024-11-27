package com.ptit.datn.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserNameDTO {
    private Long id;
    private String login;
    private String fullName;
    private String imageSignature;
    private String base64Image;

    public UserNameDTO(Long id, String login, String fullName, String imageSignature) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.imageSignature = imageSignature;
    }
}
