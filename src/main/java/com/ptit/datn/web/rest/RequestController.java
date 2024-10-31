package com.ptit.datn.web.rest;

import com.ptit.datn.service.RequestService;
import com.ptit.datn.service.dto.RequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
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
