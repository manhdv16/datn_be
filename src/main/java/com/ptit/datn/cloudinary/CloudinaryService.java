package com.ptit.datn.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ptit.datn.exception.AppException;
import com.ptit.datn.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service for uploading files to cloudinary
 */
@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    /**
     * Upload file to cloudinary
     * @param file
     * @return url of the uploaded file
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new AppException(ErrorCode.URL_NOT_FOUND);
        }

    }
}
