package com.ptit.datn.service;

import com.ptit.datn.cloudinary.CloudinaryService;
import com.ptit.datn.domain.Building;
import com.ptit.datn.domain.BuildingImage;
import com.ptit.datn.domain.Image;
import com.ptit.datn.domain.User;
import com.ptit.datn.domain.key.BuildingImageId;
import com.ptit.datn.dto.request.BuildingCreateRequest;
import com.ptit.datn.dto.request.BuildingUpdateRequest;
import com.ptit.datn.repository.*;
import com.ptit.datn.security.SecurityUtils;
import com.ptit.datn.service.dto.BuildingDTO;
import com.ptit.datn.service.dto.ImageDTO;
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
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
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
    private final UserRepository userRepository;
    private final UserBuildingRepository userBuildingRepository;

    private final CloudinaryService cloudinaryService;

    public BuildingService(BuildingRepository buildingRepository,
                           WardRepository wardRepository,
                           OfficeRepository officeRepository,
                           ImageRepository imageRepository,
                           BuildingImageRepository buildingImageRepository,
                           UserRepository userRepository,
                            UserBuildingRepository userBuildingRepository,
                           CloudinaryService cloudinaryService) {
        this.buildingRepository = buildingRepository;
        this.wardRepository = wardRepository;
        this.officeRepository = officeRepository;
        this.cloudinaryService = cloudinaryService;
        this.imageRepository = imageRepository;
        this.buildingImageRepository = buildingImageRepository;
        this.userBuildingRepository = userBuildingRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<BuildingDTO> getAllBuildingsUnpaged() {
        log.info("Get all buildings");
        User user = userRepository.findById(Long.valueOf(SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AuthorizationDeniedException("Không tìm thấy người dùng hiện tại",
                    new AuthorizationDecision(false)))))
            .orElseThrow(() -> new AuthorizationDeniedException("Không tìm thấy người dùng hiện tại",
                new AuthorizationDecision(false)));

        boolean isManager = user.getAuthorities().stream().anyMatch(role -> role.getName().equals("ROLE_MANAGER"));
        boolean isAdmin = user.getAuthorities().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!isManager && !isAdmin) {
            throw new AuthorizationDeniedException("Bạn không có quyền truy cập", new AuthorizationDecision(false));
        }

        Specification spec = (root, query, cb) -> {
            if (isManager && !isAdmin) {
                Set<Long> buildingIds = userBuildingRepository.findByUserId(user.getId()).stream()
                    .map(userBuilding -> userBuilding.getBuildingId())
                    .collect(Collectors.toSet());
                return cb.and(root.get("id").in(buildingIds));
            }
            return cb.conjunction();
        };
        List<Building> buildings = buildingRepository.findAll(spec);
        return buildings.stream().map(BuildingDTO::new).collect(Collectors.toList());
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
            query.distinct(true);
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
            buildingDTO.setImages(buildingImageRepository.findAllByIdBuildingId(buildingDTO.getId()).stream()
                    .map(buildingImage -> imageRepository.findById(buildingImage.getId().getImageId()).orElseThrow())
                    .map(ImageDTO::new)
                    .collect(Collectors.toList()));
        });
        return buildingDTOS;
    }

    @Transactional(readOnly = true)
    public Page<BuildingDTO> getBuildingsForManage(Pageable pageable,
                                                    String keyword,
                                                    Long wardId,
                                                    Long districtId,
                                                    Long provinceId,
                                                    BigInteger minPrice,
                                                    BigInteger maxPrice,
                                                    Double minArea,
                                                    Double maxArea) {
        log.info("Get buildings for manager");

        // Get current user
        User user = userRepository.findById(Long.valueOf(SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AuthorizationDeniedException("Không tìm thấy người dùng hiện tại",
                    new AuthorizationDecision(false)))))
            .orElseThrow(() -> new AuthorizationDeniedException("Không tìm thấy người dùng hiện tại",
                new AuthorizationDecision(false)));

        // Check if user is manager or admin
        boolean isManager = user.getAuthorities().stream().anyMatch(role -> role.getName().equals("ROLE_MANAGER"));
        boolean isAdmin = user.getAuthorities().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!isManager && !isAdmin) {
            throw new AuthorizationDeniedException("Bạn không có quyền truy cập", new AuthorizationDecision(false));
        }

        // Get all building ids that the user is manager
        Set<Long> buildingIds = userBuildingRepository.findByUserId(user.getId()).stream()
                .map(userBuilding -> userBuilding.getBuildingId())
                .collect(Collectors.toSet());
        if (buildingIds.isEmpty() && !isAdmin) {
            return Page.empty();
        }

        Specification<Building> spec = (root, query, cb) -> {
            // Join with the offices table to filter by price/area
            Join<Object, Object> officeJoin = root.join("offices", JoinType.LEFT);
            query.distinct(true);
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

            if (isManager && !isAdmin) {
                predicate = cb.and(predicate, root.get("id").in(buildingIds));
            }

            return predicate;
        };

        Page<Building> buildings = buildingRepository.findAll(spec, pageable);
        Page<BuildingDTO> buildingDTOS = buildings.map(BuildingDTO::new);
        buildingDTOS.forEach(buildingDTO -> {
            buildingDTO.setImages(buildingImageRepository.findAllByIdBuildingId(buildingDTO.getId()).stream()
                    .map(buildingImage -> imageRepository.findById(buildingImage.getId().getImageId()).orElseThrow())
                    .map(ImageDTO::new)
                    .collect(Collectors.toList()));
        });
        return buildingDTOS;
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
        buildingDTO.setImages(buildingImageRepository.findAllByIdBuildingId(id).stream()
                .map(buildingImage -> imageRepository.findById(buildingImage.getId().getImageId()).orElseThrow())
                .map(ImageDTO::new)
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

        List<ImageDTO> imageDTOs = new ArrayList<>();
        List<Image> addedImages = new ArrayList<>();
        try {
            Arrays.stream(images).toList().forEach(image -> {
                Map imageMap = cloudinaryService.uploadFile(image);
                Image img = new Image();
                img.setUrl((String) imageMap.get("url"));
                img.setPublicId((String) imageMap.get("public_id"));
                Image addedImage = imageRepository.save(img);
                addedImages.add(addedImage);

                imageDTOs.add(new ImageDTO(addedImage));

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

        buildingDTO.setImages(imageDTOs);
        return buildingDTO;
    }

    public BuildingDTO updateBuilding(BuildingUpdateRequest buildingUpdateRequest, MultipartFile[] newImages) {
        log.info("Update building");
        Building building = buildingRepository.findById(buildingUpdateRequest.getId()).orElseThrow();
        building.setName(buildingUpdateRequest.getName());
        building.setAddress(buildingUpdateRequest.getAddress());
        building.setWard(wardRepository.findById(buildingUpdateRequest.getWardId()).orElseThrow());
        building.setNumberOfFloor(buildingUpdateRequest.getNumberOfFloor());
        building.setNumberOfBasement(buildingUpdateRequest.getNumberOfBasement());
        building.setPricePerM2(buildingUpdateRequest.getPricePerM2());
        building.setFloorHeight(buildingUpdateRequest.getFloorHeight());
        building.setFloorArea(buildingUpdateRequest.getFloorArea());
        building.setFacilities(buildingUpdateRequest.getFacilities());
        building.setCarParkingFee(buildingUpdateRequest.getCarParkingFee());
        building.setMotorbikeParkingFee(buildingUpdateRequest.getMotorbikeParkingFee());
        building.setSecurityFee(buildingUpdateRequest.getSecurityFee());
        building.setCleaningFee(buildingUpdateRequest.getCleaningFee());
        building.setNote(buildingUpdateRequest.getNote());

        // Delete images
        if (buildingUpdateRequest.getDeletedImages() != null && !buildingUpdateRequest.getDeletedImages().isEmpty()) {
            buildingUpdateRequest.getDeletedImages().forEach(imageId -> {
                Image image = imageRepository.findById(imageId).orElseThrow();
                cloudinaryService.deleteFile(image.getPublicId());
                imageRepository.delete(image);
                buildingImageRepository.deleteById(new BuildingImageId(building.getId(), imageId));
            });
        }

        // Add new images
        if (newImages != null && newImages.length > 0) {
            List<ImageDTO> imageDTOs = new ArrayList<>();
            List<Image> addedImages = new ArrayList<>();
            try {
                Arrays.stream(newImages).toList().forEach(image -> {
                    Map imageMap = cloudinaryService.uploadFile(image);
                    Image img = new Image();
                    img.setUrl((String) imageMap.get("url"));
                    img.setPublicId((String) imageMap.get("public_id"));
                    Image addedImage = imageRepository.save(img);
                    addedImages.add(addedImage);

                    imageDTOs.add(new ImageDTO(addedImage));

                    BuildingImage buildingImage = new BuildingImage();
                    buildingImage.setId(new BuildingImageId(building.getId(), img.getId()));
                    buildingImageRepository.save(buildingImage);
                });
            } catch (Exception e) {
                log.error("Error when upload image: " + e.getMessage());
                addedImages.forEach(image -> {
                    cloudinaryService.deleteFile(image.getPublicId());
                    imageRepository.delete(image);
                });
                throw new RollbackException("Error when upload image");
            }
        }

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
