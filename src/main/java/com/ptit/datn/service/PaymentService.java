package com.ptit.datn.service;

import com.ptit.datn.config.VNPAYConfig;
import com.ptit.datn.dto.response.PaymentDTO;
import com.ptit.datn.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;

    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));

        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        // Sắp xếp các tham số theo thứ tự bảng chữ cái và nối thành chuỗi
        String hashData = VNPayUtil.buildQueryString(vnpParamsMap, false);

        // Tạo vnp_SecureHash bằng HMAC SHA-512
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        // Thêm vnp_SecureHash vào query URL
        vnpParamsMap.put("vnp_SecureHash", vnpSecureHash);
        String queryUrl = VNPayUtil.buildQueryString(vnpParamsMap, true);

        // Tạo đường dẫn thanh toán đầy đủ
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        // Trả về kết quả
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();

    }
}
