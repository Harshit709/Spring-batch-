package com.example.batch.constants;

public class BatchConstants {

    public static final String FILE_PATH_PARAM = "filePath";
    public static final String FIELD_NAMES_PARAM = "fieldNames";
    public static final String TARGET_CLASS_PARAM = "targetClass";
    public static final String PRODUCT_NAMES ="id,title,description,price,discount";
    public static final String STUDENT_NAMES ="id,name,email";
    public static final String JOB_NAME = "importJob";
    public static final String STUDENT = "student";
    public static final String STEP_NAME = "genericStep1";
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
    public static final String JOBS_BASE_PATH = "/jobs";
    public static final String IMPORT_DATA_PATH = "/importData";
    public static final String FILE_EMPTY_ERROR = "File is empty";
    public static final String FILE_NOT_CSV_ERROR = "File must be a CSV";
    public static final String FILE_CONTENT_INVALID_ERROR = "File content is invalid: missing required fields";
    public static final String FILE_CONTENT_READ_ERROR = "Failed to read file content";
    public static final String CSV_EXTENSION = ".csv";

    private BatchConstants() {
        // Private constructor to prevent instantiation
    }
}
