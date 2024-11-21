package com.ptit.datn.web.rest;

import com.ptit.datn.dto.response.ApiResponse;
import com.ptit.datn.service.SignatureService;
import com.ptit.datn.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/signature")
public class SignatureController {

    UserService userService;

    // API nhận file từ FE
    @PostMapping("/verify")
    public ApiResponse<Boolean> verifySignature(@RequestParam("file") MultipartFile file) {
        try {
            // Sinh giá trị hash từ file tải lên
            String uploadedHash = SignatureService.generateHashFromMultipartFile(file);

            String storedHash = userService.getDigitalSignature();

            // So sánh hash
            Boolean isMatch = SignatureService.compareHashes(uploadedHash, storedHash);
            return ApiResponse.<Boolean>builder()
                .code(isMatch ? 200 : 1001)
                .result(isMatch)
                .message(isMatch ? "Verify signature successfully" : "Verify signature failed")
                .build();
        } catch (Exception e) {
            return ApiResponse.<Boolean>builder()
                .message("Verify signature failed")
                .code(1001)
                .result(false)
                .build();
        }
    }
}

