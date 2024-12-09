package com.ptit.datn.web.rest;

import com.ptit.datn.dto.response.CommonResponse;
import com.ptit.datn.service.ContractService;
import com.ptit.datn.service.dto.BuildingContractStatDTO;
import com.ptit.datn.service.dto.ContractDTO;
import com.ptit.datn.service.dto.FilterDTO;
import com.ptit.datn.service.dto.model.PageFilterInput;
//import com.ptit.datn.utils.Constants;
import com.ptit.datn.constants.Constants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractResource {
    private final ContractService contractService;

    @PostMapping("")
    public CommonResponse<List<ContractDTO>> getAll(@RequestBody PageFilterInput<List<FilterDTO>> input){
        Page<ContractDTO> result = contractService.getAll(input, Constants.FilterOperator.AND);
        return new CommonResponse<List<ContractDTO>>()
            .success()
            .data(result.getContent())
            .totalElements(result.getTotalElements());
    }

    @PostMapping("/filter-user")
    public CommonResponse<List<ContractDTO>> getAllByUser(@RequestBody PageFilterInput<List<FilterDTO>> input){
        Page<ContractDTO> result = contractService.getAll(input, Constants.FilterOperator.OR);
        return new CommonResponse<List<ContractDTO>>()
            .success()
            .data(result.getContent())
            .totalElements(result.getTotalElements());
    }



    @GetMapping("/{id}")
    public CommonResponse<ContractDTO> getDetail(@PathVariable Long id){
        ContractDTO result = contractService.getDetail(id);
        return new CommonResponse<ContractDTO>()
            .success()
            .data(result);
    }

    @PostMapping("/save")
    public CommonResponse saveContract(@RequestBody ContractDTO contractDTO){
        Long contractId = contractService.saveContract(contractDTO);
        return new CommonResponse()
            .result(Constants.HTTP_STATUS.CREATED, "message.created")
            .data(contractId);
    }

    @PutMapping("/update/{id}")
    public CommonResponse updateContract(@PathVariable Long id, @RequestBody ContractDTO contractDTO){
        Long contractId = contractService.updateContract(id, contractDTO);
        return new CommonResponse()
            .result(Constants.HTTP_STATUS.CREATED, "message.created")
            .data(contractId);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResponse deleteContract(@PathVariable Long id){
        contractService.deleteContract(id);
        return new CommonResponse().success();
    }

    @PostMapping("/{contractId}/verify-signature")
    public CommonResponse<?> verifySignature(@RequestParam("file") MultipartFile file,
                                             @PathVariable Long contractId){
        contractService.verifySigner(contractId, file);
        return new CommonResponse<>().success();
    }

    @GetMapping("/{id}/export-pdf")
    public void exportPdf(HttpServletResponse response, @PathVariable Long id) throws IOException {
        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "inline; filename=contract.pdf";
        response.setHeader(headerKey, headerValue);
        contractService.exportPdf(response.getOutputStream() ,id);
    }

    @PostMapping("/stat-building-contracts")
    public CommonResponse<?> getStatBuildingContract(@RequestBody BuildingContractStatDTO input){
        List<BuildingContractStatDTO> result = contractService.getStatBuildingContract(input);
        return new CommonResponse<>().success().data(result);
    }


}
