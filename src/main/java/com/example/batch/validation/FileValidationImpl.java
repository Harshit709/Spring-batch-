package com.example.batch.validation;

import com.example.batch.constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FileValidationImpl implements FileValidation {

    private static final Logger logger = LoggerFactory.getLogger(FileValidationImpl.class);

    @Override
    public String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return BatchConstants.FILE_EMPTY_ERROR;
        }

        String filename = Objects.requireNonNull(file.getOriginalFilename());
        if (!StringUtils.hasText(filename) || !filename.toLowerCase().endsWith(BatchConstants.CSV_EXTENSION)) {
            return BatchConstants.FILE_NOT_CSV_ERROR;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return BatchConstants.FILE_CONTENT_INVALID_ERROR;
            }

            // Check if header line matches either the product or student format
            if (!headerLine.equals(BatchConstants.PRODUCT_NAMES) && !headerLine.equals(BatchConstants.STUDENT_NAMES)) {
                return BatchConstants.FILE_CONTENT_INVALID_ERROR;
            }

        } catch (Exception e) {
            logger.error("Error reading file content", e);
            return BatchConstants.FILE_CONTENT_READ_ERROR;
        }

        return null;
    }
}
