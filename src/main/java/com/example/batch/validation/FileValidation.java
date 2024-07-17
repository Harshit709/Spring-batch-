package com.example.batch.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileValidation {
    public String validateFile(MultipartFile file);
}
