package com.threadly.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.url}")
    String cloudinaryUrl;

    @Bean
    public Cloudinary getCloudinaryUrl() {
        return new Cloudinary(cloudinaryUrl);
    }
}
