package com.ptit.datn.web.rest;

import com.ptit.datn.dto.request.StatusAcceptRequest;
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
    public ResponseEntity<Page<RequestDTO>> getAllRequests(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                           @RequestParam(value = "status", required = false) Integer status,
                                                           @RequestParam(value = "userId", required = false) Long userId) {
        Sort sort = Sort.by(List.of(Sort.Order.asc("status"),
            Sort.Order.desc("createdDate")));
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok().body(requestService.getAllRequests(pageable, status, userId));
    }

    @GetMapping("/manage-list")
    public ResponseEntity<Page<RequestDTO>> getAllRequestsForManage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                    @RequestParam(value = "buildingId", required = false) Long buildingId,
                                                                    @RequestParam(value = "status", required = false) Integer status) {
        Sort sort = Sort.by(List.of(Sort.Order.asc("status"),
            Sort.Order.desc("createdDate")));
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok().body(requestService.getAllRequestsForManage(pageable, status, buildingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDTO> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok().body(requestService.getRequestById(id));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<RequestDTO>> getAllRequestsByUser(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                 @RequestParam(value = "status", required = false) Integer status) {
        Sort sort = Sort.by(List.of(Sort.Order.asc("status"),
            Sort.Order.desc("createdDate")));
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok().body(requestService.getAllRequestsByUser(pageable, status));
    }


    @PostMapping
    public ResponseEntity<RequestDTO> createRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.created(null).body(requestService.createRequest(requestDTO));
    }

    @PutMapping
    public ResponseEntity<RequestDTO> updateRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok().body(requestService.updateRequest(requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        return ResponseEntity.ok().body(requestService.deleteRequest(id));
    }

    //region handle status changed
    @PostMapping("/handle-accept/{id}")
    public ResponseEntity<RequestDTO> handleAccept(@PathVariable Long id, @RequestBody StatusAcceptRequest req) {
        return ResponseEntity.ok().body(requestService.handleAccept(id, req));
    }

    @PostMapping("/handle-reject/{id}")
    public ResponseEntity<RequestDTO> handleReject(@PathVariable Long id) {
        return ResponseEntity.ok().body(requestService.handleReject(id));
    }

    @PostMapping("/handle-cancel/{id}")
    public ResponseEntity<RequestDTO> handleCancel(@PathVariable Long id) {
        return ResponseEntity.ok().body(requestService.handleCancel(id));
    }

    @PostMapping("/handle-complete/{id}")
    public ResponseEntity<RequestDTO> handleComplete(@PathVariable Long id) {
        return ResponseEntity.ok().body(requestService.handleComplete(id));
    }
    //endregion
}
