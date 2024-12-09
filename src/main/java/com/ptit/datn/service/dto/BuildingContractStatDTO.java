package com.ptit.datn.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BuildingContractStatDTO implements Serializable {
    private Long buildingId;
    private String buildingName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfContracts;
    private Double revenue;

    public BuildingContractStatDTO(Long buildingId, String buildingName, Integer numberOfContracts) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.numberOfContracts = numberOfContracts;
    }
}
