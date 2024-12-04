package com.ptit.datn.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class BuildingUpdateRequest {
    private Long id;
    private String name;

    private Long wardId;

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

    private List<Long> deletedImages;
}
