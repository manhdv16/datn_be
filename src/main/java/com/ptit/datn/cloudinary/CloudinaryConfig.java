package com.ptit.datn.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cloudinary configuration.
 */
@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "djknejf5i",
                "api_key", "612448547275481",
                "api_secret", "wzIMvKlkfBIJJWCJX9Qa1LzjsZk",
                "secure", true
        ));
        return cloudinary;
    }
}
