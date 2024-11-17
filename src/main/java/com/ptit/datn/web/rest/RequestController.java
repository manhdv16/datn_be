package com.ptit.datn.web.rest;

import com.ptit.datn.service.RequestService;
import com.ptit.datn.service.dto.RequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<Page<RequestDTO>> getAllRequests(@RequestParam(value = "page", defaultValue = "0") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        Sort sort = Sort.by(List.of(Sort.Order.asc("status"),
            Sort.Order.desc("date"),
            Sort.Order.desc("time")));
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok().body(requestService.getAllRequests(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDTO> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok().body(requestService.getRequestById(id));
    }

    @PostMapping
    public ResponseEntity<RequestDTO> createRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.created(null).body(requestService.createRequest(requestDTO));
    }

    @PutMapping
    public ResponseEntity<RequestDTO> updateRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok().body(requestService.updateRequest(requestDTO));
    }
}
