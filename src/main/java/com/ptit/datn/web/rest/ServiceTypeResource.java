package com.ptit.datn.web.rest;

import com.ptit.datn.constants.Constants;
import com.ptit.datn.dto.response.CommonResponse;
import com.ptit.datn.service.ServiceTypeService;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.ServiceTypeDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-type")
@RequiredArgsConstructor
public class ServiceTypeResource {
    private final ServiceTypeService serviceTypeService;

    @PostMapping("")
    public CommonResponse<List<ServiceTypeDTO>> getAll(@RequestBody PageFilterInput<List<FilterDTO>> input){
        Page<ServiceTypeDTO> result = serviceTypeService.getAll(input);
        return new CommonResponse<List<ServiceTypeDTO>>()
            .success()
            .data(result.getContent())
            .totalElements(result.getTotalElements());
    }

    @GetMapping("/{id}")
    public CommonResponse<ServiceTypeDTO> getDetail(@PathVariable Integer id){
        ServiceTypeDTO result = serviceTypeService.getDetail(id);
        return new CommonResponse<ServiceTypeDTO>()
            .success()
            .data(result);
    }

    @PostMapping("/save")
    public CommonResponse saveServiceType(@RequestBody ServiceTypeDTO input){
        Integer result = serviceTypeService.saveServiceType(input);
        return new CommonResponse()
            .result(Constants.HTTP_STATUS.CREATED, "message.created")
            .data(result);
    }

    @PutMapping("/update/{id}")
    public CommonResponse updateServiceType(@PathVariable Integer id, @RequestBody ServiceTypeDTO input){
        Integer result = serviceTypeService.updateServiceType(id, input);
        return new CommonResponse()
            .result(Constants.HTTP_STATUS.OK, "message.created")
            .data(result);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResponse deleteServiceType(@PathVariable Integer id){
        serviceTypeService.deleteServiceType(id);
        return new CommonResponse().success();
    }
}
