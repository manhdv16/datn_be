package com.ptit.datn.repository;

import com.ptit.datn.domain.BuildingImage;
import com.ptit.datn.domain.key.BuildingImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingImageRepository extends JpaRepository<BuildingImage, BuildingImageId> {

    @Modifying
    @Query("delete from BuildingImage bi where bi.id.buildingId = ?1")
    void deleteAllByBuildingId(Long buildingId);

    List<BuildingImage> findAllByIdBuildingId(Long buildingId);
}
