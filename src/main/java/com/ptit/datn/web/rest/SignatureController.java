package com.ptit.datn.web.rest;

import com.ptit.datn.service.SignatureService;
import com.ptit.datn.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/signature")
public class SignatureController {

    UserService userService;

    // API nhận file từ FE
    @PostMapping("/verify")
    public boolean verifySignature(@RequestParam("file") MultipartFile file) {
        try {
            // Sinh giá trị hash từ file tải lên
            String uploadedHash = SignatureService.generateHashFromMultipartFile(file);

            String storedHash = userService.getDigitalSignature();

            // So sánh hash
            return SignatureService.compareHashes(uploadedHash, storedHash);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

