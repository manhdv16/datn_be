package com.ptit.datn.service.dto;

import com.ptit.datn.domain.Office;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class OfficeDTO {
    private Long id;
    private Double area;
    private Integer floor;
    private BigInteger rentalPrice;
    private Long buildingId;
    private String note;

    public OfficeDTO() {
        // Empty constructor needed for Jackson.
    }

    public OfficeDTO(Office office) {
        this.id = office.getId();
        this.area = office.getArea();
        this.floor = office.getFloor();
        this.rentalPrice = office.getRentalPrice();
        this.buildingId = office.getBuildingId();
        this.note = office.getNote();
    }

}
