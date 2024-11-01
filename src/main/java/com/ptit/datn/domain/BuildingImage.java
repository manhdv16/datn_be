package com.ptit.datn.domain;

import com.ptit.datn.domain.key.BuildingImageId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "building_image")
@Getter
@Setter
public class BuildingImage {

    @EmbeddedId
    private BuildingImageId id;

}
