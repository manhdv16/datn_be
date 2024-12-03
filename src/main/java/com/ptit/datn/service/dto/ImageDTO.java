package com.ptit.datn.service.dto;

import com.ptit.datn.domain.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDTO {
    private Long id;
    private String publicId;
    private String url;


    public ImageDTO() {
    }

    public ImageDTO(Long id, String publicId, String url) {
        this.id = id;
        this.publicId = publicId;
        this.url = url;
    }

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.publicId = image.getPublicId();
        this.url = image.getUrl();
    }
}
