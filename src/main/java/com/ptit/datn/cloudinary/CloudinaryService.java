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
}
