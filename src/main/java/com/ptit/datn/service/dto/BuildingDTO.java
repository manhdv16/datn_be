package com.ptit.datn.service.dto;

import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.Office;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class BuildingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private Long id;

    private String name;

    private String address;

    private String facilities;

    private String note;

    private Set<Long> officeIds;

    public BuildingDTO() {
        // Empty constructor needed for Jackson.
    }

    public BuildingDTO(Building building) {
        this.id = building.getId();
        this.name = building.getName();
        this.address = building.getAddress();
        this.facilities = building.getFacilities();
        this.note = building.getNote();
    }

}
