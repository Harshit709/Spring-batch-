package com.example.batch.controller;

import com.example.batch.constants.BatchConstants;
import com.example.batch.entity.Student;
import com.example.batch.mapping.ProductByFile;

import com.example.batch.validation.FileValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@RestController
@RequestMapping(BatchConstants.JOBS_BASE_PATH)
public class BatchController {

    private final FileValidation validation;

    public BatchController(FileValidation fileValidation) {
        this.validation = fileValidation;
    }

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importEntitiesJob;

    @PostMapping(path = BatchConstants.IMPORT_DATA_PATH, consumes = BatchConstants.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importData(@RequestPart("file") MultipartFile file) {
        try {
            logger.info("Received file with name: {}", file.getOriginalFilename());
            logger.info("Received file with content type: {}", file.getContentType());
            String validationError = validation.validateFile(file);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(validationError);
            }

            Path tempDir = Files.createTempDirectory("");
            Path tempFilePath = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.write(tempFilePath, file.getBytes());
            String filePath = tempFilePath.toString();
            String targetClassName = ProductByFile.class.getName();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString(BatchConstants.FILE_PATH_PARAM, filePath)
                    .addString(BatchConstants.FIELD_NAMES_PARAM, BatchConstants.PRODUCT_NAMES)
                    .addString(BatchConstants.TARGET_CLASS_PARAM, targetClassName)
                    .toJobParameters();

            jobLauncher.run(importEntitiesJob, jobParameters);
            return ResponseEntity.ok("Job started successfully");
        } catch (Exception e) {
            logger.error("Error starting job", e);
            return ResponseEntity.status(500).body("Job failed to start");
        }
    }
    @PostMapping(path =BatchConstants.STUDENT, consumes = BatchConstants.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> demo(@RequestPart("file") MultipartFile file) {
        try {
            logger.info("Received file with name: {}", file.getOriginalFilename());
            logger.info("Received file with content type: {}", file.getContentType());
             String validationError = validation.validateFile(file);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(validationError);
            }

            Path tempDir = Files.createTempDirectory("");
            Path tempFilePath = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.write(tempFilePath, file.getBytes());
            String filePath = tempFilePath.toString();
            String targetClassName = Student.class.getName();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString(BatchConstants.FILE_PATH_PARAM, filePath)
                    .addString(BatchConstants.FIELD_NAMES_PARAM, BatchConstants.STUDENT_NAMES)
                    .addString(BatchConstants.TARGET_CLASS_PARAM, targetClassName)
                    .toJobParameters();

            jobLauncher.run(importEntitiesJob, jobParameters);
            return ResponseEntity.ok("Job started successfully");
        } catch (Exception e) {
            logger.error("Error starting job", e);
            return ResponseEntity.status(500).body("Job failed to start");
        }
    }
}