package com.ptit.datn.service;

import com.ptit.datn.domain.Office;
import com.ptit.datn.repository.OfficeRepository;
import com.ptit.datn.service.dto.OfficeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OfficeService {

    private static final Logger log = LoggerFactory.getLogger(OfficeService.class);

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    @Transactional(readOnly = true)
    public Page<OfficeDTO> getOffices(Pageable pageable) {
        log.info("Get offices");
        return officeRepository.findAll(pageable).map(OfficeDTO::new);
    }

    @Transactional(readOnly = true)
    public OfficeDTO getOffice(Long id) {
        log.info("Get office");
        return officeRepository.findById(id).map(OfficeDTO::new).orElseThrow();
    }

    public Office createOffice(OfficeDTO officeDTO) {
        log.info("Create office");
        Office office = Office.builder()
                .area(officeDTO.getArea())
                .floor(officeDTO.getFloor())
                .rentalPrice(officeDTO.getRentalPrice())
                .buildingId(officeDTO.getBuildingId())
                .note(officeDTO.getNote())
                .build();
        return officeRepository.save(office);
    }

    public Office updateOffice(OfficeDTO officeDTO) {
        log.info("Update office");
        Office office = officeRepository.findById(officeDTO.getId()).orElseThrow();
        office.setArea(officeDTO.getArea());
        office.setFloor(officeDTO.getFloor());
        office.setRentalPrice(officeDTO.getRentalPrice());
        office.setBuildingId(officeDTO.getBuildingId());
        office.setNote(officeDTO.getNote());
        return officeRepository.save(office);
    }

    public void deleteOffice(Long id) {
        log.info("Delete office");
        officeRepository.deleteById(id);
    }
}
