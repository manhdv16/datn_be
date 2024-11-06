package com.ptit.datn.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class BuildingCreateRequest {
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

    public BuildingCreateRequest() {

    }
}
