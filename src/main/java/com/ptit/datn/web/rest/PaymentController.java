package com.ptit.datn.web.rest;


import com.ptit.datn.domain.ContractEntity;
import com.ptit.datn.dto.response.ApiResponse;
import com.ptit.datn.dto.response.PaymentDTO;
import com.ptit.datn.service.ContractService;
import com.ptit.datn.service.PaymentService;
import com.ptit.datn.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final ContractService contractService;
    @GetMapping("/vn-pay")
    public ApiResponse<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return ApiResponse.<PaymentDTO.VNPayResponse>builder()
            .message("Success")
            .result(paymentService.createVnPayPayment(request))
            .build();
    }
    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String amount = request.getParameter("vnp_Amount");
//        Long contractId = Long.valueOf(request.getParameter("contract_id"));
        String strContractId = request.getParameter("vnp_TxnRef").split("_")[1];
        Long contractId = Long.valueOf(strContractId);
        System.out.println("contractId: " + contractId);

        // thêm dk check để update status cho thanh toán
        if(status.equals("00")){
            contractService.changeContractStatus(contractId, Constants.PaymentStatus.PAID);
        }

        String redirectUrl = "https://office-nest-fe-83366.ondigitalocean.app/payment-result"
            + "?vnp_ResponseCode=" + status
            + "&vnp_TransactionNo=" + transactionNo
            + "&vnp_Amount=" + amount;
        response.sendRedirect(redirectUrl);
    }
}

