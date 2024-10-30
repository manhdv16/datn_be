package com.ptit.datn.service.dto;

import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Ward;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class BuildingDTO {

    private Long id;
    private String name;

    private Long wardId;
    private Ward ward;

    private String address;
    private Integer numberOfFloor;
    private Integer numberOfBasement;
    private BigInteger pricePerM2;
    private Double floorHeight;
    private Double floorArea;
    private String facilities;
    private String note;
    private BigInteger carParkingFee;
    private BigInteger motorbikeParkingFee;
    private BigInteger securityFee;
    private BigInteger cleaningFee;
    private List<OfficeDTO> officeDTOS;

    public BuildingDTO() {

    }

    public BuildingDTO(Building building) {
        this.id = building.getId();
        this.name = building.getName();
        if (building.getWard() != null) {
            this.wardId = building.getWard().getId();
            this.ward = building.getWard();
        }
        this.address = building.getAddress();
        this.numberOfFloor = building.getNumberOfFloor();
        this.numberOfBasement = building.getNumberOfBasement();
        this.pricePerM2 = building.getPricePerM2();
        this.floorHeight = building.getFloorHeight();
        this.floorArea = building.getFloorArea();
        this.facilities = building.getFacilities();
        this.carParkingFee = building.getCarParkingFee();
        this.motorbikeParkingFee = building.getMotorbikeParkingFee();
        this.securityFee = building.getSecurityFee();
        this.cleaningFee = building.getCleaningFee();
        this.note = building.getNote();
    }
}
