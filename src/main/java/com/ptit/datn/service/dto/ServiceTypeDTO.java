package com.ptit.datn.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class ServiceTypeDTO {
    private Integer id;
    private String name;
    private String description;
    private String unit;
    private String createdBy;
    private Instant lastModifiedDate;
    private Integer category;

    public ServiceTypeDTO(Integer id, String name, String description, String unit, String createdBy, Instant lastModifiedDate, Integer category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.createdBy = createdBy;
        this.lastModifiedDate = lastModifiedDate;
        this.category = category;
    }
}
