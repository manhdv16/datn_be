package com.ptit.datn.service.dto;

import com.ptit.datn.domain.Office;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class OfficeDTO {

    private Long id;
    private String name;
    private Double area;
    private Integer floor;
    private BigInteger price;
    private String note;
    private Integer status;
    private Long buildingId;
    private BuildingDTO building;



    public OfficeDTO() {

    }

    public OfficeDTO(Office office) {
        this.id = office.getId();
        this.name = office.getName();
        this.area = office.getArea();
        this.floor = office.getFloor();
        this.price = office.getPrice();
        this.note = office.getNote();
        this.status = office.getStatus();
        if (office.getBuilding() != null) {
            this.buildingId = office.getBuilding().getId();
            this.building = new BuildingDTO(office.getBuilding());
        }
    }
}
