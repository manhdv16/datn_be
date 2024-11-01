package com.ptit.datn.domain.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BuildingImageId implements Serializable {

    private Long buildingId;
    private Long imageId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BuildingImageId that = (BuildingImageId) obj;

        return buildingId.equals(that.buildingId) && imageId.equals(that.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildingId, imageId);
    }
}
