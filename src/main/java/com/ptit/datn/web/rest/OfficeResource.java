package com.ptit.datn.web.rest;

import com.ptit.datn.service.OfficeService;
import com.ptit.datn.service.dto.OfficeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/offices")
public class OfficeResource {

    private static final Logger log = LoggerFactory.getLogger(OfficeResource.class);

    private final OfficeService officeService;

    public OfficeResource(OfficeService officeService) {
        this.officeService = officeService;
    }

    @GetMapping
    public ResponseEntity<Page<OfficeDTO>> getOffices(@RequestParam(defaultValue = "0") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      @RequestParam(required = false) String search,
                                                      @RequestParam(required = false) Long buildingId,
                                                      @RequestParam(required = false) Long wardId,
                                                      @RequestParam(required = false) Long districtId,
                                                      @RequestParam(required = false) Long provinceId,
                                                      @RequestParam(required = false) BigInteger minPrice,
                                                      @RequestParam(required = false) BigInteger maxPrice,
                                                      @RequestParam(required = false) Double minArea,
                                                      @RequestParam(required = false) Double maxArea,
                                                      @RequestParam(required = false) Integer status
                                                      ) {
        log.info("REST request to get a page of offices");
        Sort sort = Sort.by(List.of(
            Sort.Order.asc("status"),
            Sort.Order.asc("building.name"),
            Sort.Order.asc("floor"),
            Sort.Order.asc("name")
        ));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OfficeDTO> list = officeService.getOffices(pageable, search, buildingId, wardId, districtId, provinceId,
            minPrice, maxPrice, minArea, maxArea, status);
        return ResponseEntity.ok().body(list);
    }


    @GetMapping("/{id}")
    public ResponseEntity<OfficeDTO> getOffice(@PathVariable Long id) {
        log.info("REST request to get office : {}", id);
        OfficeDTO officeDTO = officeService.getOffice(id);
        return ResponseEntity.ok().body(officeDTO);
    }

    @PostMapping
    public ResponseEntity<OfficeDTO> createOffice(@RequestBody OfficeDTO officeDTO) {
        log.info("REST request to save office : {}", officeDTO);
        OfficeDTO result = officeService.createOffice(officeDTO);
        return ResponseEntity.created(null).body(result);
    }

    @PutMapping
    public ResponseEntity<OfficeDTO> updateOffice(@RequestBody OfficeDTO officeDTO) {
        log.info("REST request to update office : {}", officeDTO);
        OfficeDTO result = officeService.updateOffice(officeDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        log.info("REST request to delete office : {}", id);
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }

}
