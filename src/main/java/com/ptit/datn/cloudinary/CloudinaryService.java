package com.ptit.datn.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

/**
 * Service for uploading files to cloudinary
 */
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload file to cloudinary
     * @param file
     * @return url of the uploaded file
     * @throws IOException
     */
    public Map uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//             return (String) uploadResult.get("url");
            return uploadResult;
        } catch (IOException e) {
            throw new AppException(ErrorCode.URL_NOT_FOUND);
        }
    }

    /**
     * Delete file from cloudinary
     * @param publicId
     * @return
     */
    public Map deleteFile(String publicId) {
        try {
            Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return deleteResult;
        } catch (IOException e) {
            throw new AppException(ErrorCode.URL_NOT_FOUND);
        }
    }

    public String getBase64FromPath(String imagePath) {
        try {
            // Tạo URL đầy đủ từ path
//            String imageUrl = cloudinary.url().secure(true).generate(imagePath);

            // Tải ảnh từ URL
            InputStream inputStream = new URL(imagePath).openStream();
            byte[] imageBytes = inputStream.readAllBytes();

            // Chuyển đổi thành Base64
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String convertImageToBase64(String imagePath) throws Exception {
        // Download image
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imagePath, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Convert image to Base64
            byte[] imageBytes = response.getBody();
            return Base64.getEncoder().encodeToString(imageBytes);
        } else {
            return null;
        }
    }
}
