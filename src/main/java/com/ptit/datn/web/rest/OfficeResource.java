package com.ptit.datn.web.rest;

import com.ptit.datn.domain.Office;
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

@RestController
@RequestMapping("/api/offices")
public class OfficeResource {

    private static final Logger log = LoggerFactory.getLogger(OfficeResource.class);

    private final OfficeService officeService;

    public OfficeResource(OfficeService officeService) {
        this.officeService = officeService;
    }

    @GetMapping
    public ResponseEntity<Page<OfficeDTO>> getOffices(@RequestParam(defaultValue = "0",
                                                                    required = false) Integer page,
                                                      @RequestParam(defaultValue = "20",
                                                          required = false) Integer size) {
        log.info("REST request to get a page of offices");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<OfficeDTO> list = officeService.getOffices(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfficeDTO> getOffice(@RequestParam Long id) {
        log.info("REST request to get office : {}", id);
        OfficeDTO officeDTO = officeService.getOffice(id);
        return ResponseEntity.ok().body(officeDTO);
    }

    @PostMapping
    public ResponseEntity<Office> createOffice(@RequestBody OfficeDTO officeDTO) {
        log.info("REST request to save office : {}", officeDTO);
        Office result = officeService.createOffice(officeDTO);
        return ResponseEntity.created(null).body(result);
    }

    @PutMapping
    public ResponseEntity<Office> updateOffice(@RequestBody OfficeDTO officeDTO) {
        log.info("REST request to update office : {}", officeDTO);
        Office result = officeService.updateOffice(officeDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        log.info("REST request to delete office : {}", id);
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }

}
