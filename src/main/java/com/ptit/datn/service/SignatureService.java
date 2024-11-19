package com.ptit.datn.service;

import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.Base64;

public class SignatureService {
    public static String generateHashFromMultipartFile(MultipartFile file) {
        try {
            // Chuyển MultipartFile thành mảng byte
            byte[] fileBytes = file.getBytes();

            // Tạo giá trị hash SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);

            // Chuyển giá trị hash thành chuỗi
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean compareHashes(String newHash, String storedHash) {
        if (newHash == null || storedHash == null) {
            return false;
        }
        return newHash.equals(storedHash);
    }
}

