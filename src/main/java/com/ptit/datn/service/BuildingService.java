package com.ptit.datn.service;

import com.ptit.datn.cloudinary.CloudinaryService;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.BuildingImage;
import com.ptit.datn.domain.Image;
import com.ptit.datn.domain.key.BuildingImageId;
import com.ptit.datn.dto.request.BuildingCreateRequest;
import com.ptit.datn.repository.*;
import com.ptit.datn.service.dto.BuildingDTO;
import com.ptit.datn.service.dto.OfficeDTO;
import jakarta.persistence.RollbackException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingService {

    private static final Logger log = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final OfficeRepository officeRepository;
    private final WardRepository wardRepository;
    private final ImageRepository imageRepository;
    private final BuildingImageRepository buildingImageRepository;

    private final CloudinaryService cloudinaryService;

    public BuildingService(BuildingRepository buildingRepository,
                           WardRepository wardRepository,
                           OfficeRepository officeRepository,
                            ImageRepository imageRepository,
                           BuildingImageRepository buildingImageRepository,
                           CloudinaryService cloudinaryService) {
        this.buildingRepository = buildingRepository;
        this.wardRepository = wardRepository;
        this.officeRepository = officeRepository;
        this.cloudinaryService = cloudinaryService;
        this.imageRepository = imageRepository;
        this.buildingImageRepository = buildingImageRepository;
    }

    @Transactional(readOnly = true)
    public Page<BuildingDTO> getBuildings(Pageable pageable,
                                          String keyword,
                                          Long wardId,
                                          Long districtId,
                                          Long provinceId,
                                          BigInteger minPrice,
                                          BigInteger maxPrice,
                                          Double minArea,
                                          Double maxArea) {
        log.info("Get buildings");

        Specification<Building> spec = (root, query, cb) -> {
            // Join with the offices table to filter by price/area
            Join<Object, Object> officeJoin = root.join("offices", JoinType.LEFT);

            Predicate predicate = cb.conjunction(); // Base condition

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), pattern);
                Predicate addressPredicate = cb.like(cb.lower(root.get("address")), pattern);
                predicate = cb.and(predicate, cb.or(namePredicate, addressPredicate));
            }

            if (wardId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("ward").get("id"), wardId));
            } else if (districtId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("ward").get("district").get("id"), districtId));
            } else if (provinceId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("ward").get("district").get("province").get("id"), provinceId));
            }

            if (minPrice != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(officeJoin.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(officeJoin.get("price"), maxPrice));
            }
            if (minArea != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(officeJoin.get("area"), minArea));
            }
            if (maxArea != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(officeJoin.get("area"), maxArea));
            }
            return predicate;
        };

        Page<Building> buildings = buildingRepository.findAll(spec, pageable);
        Page<BuildingDTO> buildingDTOS = buildings.map(BuildingDTO::new);
        buildingDTOS.forEach(buildingDTO -> {
            buildingDTO.setImageUrls(buildingImageRepository.findAllByIdBuildingId(buildingDTO.getId()).stream()
                    .map(buildingImage -> imageRepository.findById(buildingImage.getId().getImageId()).orElseThrow().getUrl())
                    .collect(Collectors.toList()));
        });
        return buildings.map(BuildingDTO::new);

    }

    @Transactional(readOnly = true)
    public BuildingDTO getBuildingById(Long id) {
        log.info("Get building");
        Optional<Building> buildingOptional = buildingRepository.findById(id);
        if (buildingOptional.isEmpty()) {
            return null;
        }
        BuildingDTO buildingDTO = new BuildingDTO(buildingOptional.orElseThrow());
        buildingDTO.setOfficeDTOS(officeRepository.findAllByBuildingId(id).stream().map(OfficeDTO::new).collect(Collectors.toList()));
        buildingDTO.setImageUrls(buildingImageRepository.findAllByIdBuildingId(id).stream()
                .map(buildingImage -> imageRepository.findById(buildingImage.getId().getImageId()).orElseThrow().getUrl())
                .collect(Collectors.toList()));
        return buildingDTO;
    }

    public BuildingDTO createBuilding(BuildingCreateRequest buildingRequest, MultipartFile[] images) {
        log.info("Create building");
        Building building = new Building();
        building.setName(buildingRequest.getName());
        building.setAddress(buildingRequest.getAddress());
        building.setWard(wardRepository.findById(buildingRequest.getWardId()).orElseThrow());
        building.setNumberOfFloor(buildingRequest.getNumberOfFloor());
        building.setNumberOfBasement(buildingRequest.getNumberOfBasement());
        building.setPricePerM2(buildingRequest.getPricePerM2());
        building.setFloorHeight(buildingRequest.getFloorHeight());
        building.setFloorArea(buildingRequest.getFloorArea());
        building.setFacilities(buildingRequest.getFacilities());
        building.setCarParkingFee(buildingRequest.getCarParkingFee());
        building.setMotorbikeParkingFee(buildingRequest.getMotorbikeParkingFee());
        building.setSecurityFee(buildingRequest.getSecurityFee());
        building.setCleaningFee(buildingRequest.getCleaningFee());
        building.setNote(buildingRequest.getNote());

        Building buildingResult = buildingRepository.save(building);

        List<String> imageUrls = new ArrayList<>();
        List<Image> addedImages = new ArrayList<>();
        try {
            Arrays.stream(images).toList().forEach(image -> {
                Map imageMap = cloudinaryService.uploadFile(image);
                Image img = new Image();
                img.setUrl((String) imageMap.get("url"));
                img.setPublicId((String) imageMap.get("public_id"));
                addedImages.add(imageRepository.save(img));

                imageUrls.add(img.getUrl());

                BuildingImage buildingImage = new BuildingImage();
                buildingImage.setId(new BuildingImageId(buildingResult.getId(), img.getId()));
                buildingImageRepository.save(buildingImage);
            });
        } catch (Exception e) {
            log.error("Error when upload image: " + e.getMessage());
            addedImages.forEach(image -> {
                cloudinaryService.deleteFile(image.getPublicId());
                imageRepository.delete(image);
            });
            buildingRepository.delete(buildingResult);
            throw new RollbackException("Error when upload image");
        }

        BuildingDTO buildingDTO = new BuildingDTO(buildingResult);
        buildingDTO.setImageUrls(imageUrls);
        return buildingDTO;
    }

    public BuildingDTO updateBuilding(BuildingDTO buildingDTO) {
        log.info("Update building");
        Building building = buildingRepository.findById(buildingDTO.getId()).orElseThrow();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setWard(wardRepository.findById(buildingDTO.getWardId()).orElseThrow());
        building.setNumberOfFloor(buildingDTO.getNumberOfFloor());
        building.setNumberOfBasement(buildingDTO.getNumberOfBasement());
        building.setPricePerM2(buildingDTO.getPricePerM2());
        building.setFloorHeight(buildingDTO.getFloorHeight());
        building.setFloorArea(buildingDTO.getFloorArea());
        building.setFacilities(buildingDTO.getFacilities());
        building.setCarParkingFee(buildingDTO.getCarParkingFee());
        building.setMotorbikeParkingFee(buildingDTO.getMotorbikeParkingFee());
        building.setSecurityFee(buildingDTO.getSecurityFee());
        building.setCleaningFee(buildingDTO.getCleaningFee());
        building.setNote(buildingDTO.getNote());
        return new BuildingDTO(buildingRepository.save(building));
    }

    public void deleteBuilding(Long id) {
        log.info("Delete building");

        // Delete all images of the building
        List<BuildingImage> buildingImages = buildingImageRepository.findAllByIdBuildingId(id);
        buildingImages.forEach(buildingImage -> {
            Image image = imageRepository.findById(buildingImage.getId().getImageId()).orElseThrow();
            cloudinaryService.deleteFile(image.getPublicId());                  // Delete image on cloudinary
            imageRepository.deleteById(buildingImage.getId().getImageId());     // Delete image in database
        });
        buildingImageRepository.deleteAllByBuildingId(id);

        // Delete building
        buildingRepository.deleteById(id);
    }
}
