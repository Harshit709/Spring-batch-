package com.example.batch.exceptionlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component
public class CustomRetryListener implements RetryListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomRetryListener.class);

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        // Log the opening of the retry operation
        logger.info("Opening retry operation. Retry context: {}", context);
        return true; // Return true to proceed with retry
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (throwable != null) {
            logger.error("Retry operation closed with an error: {}", throwable.getMessage(), throwable);
        } else {
            logger.info("Retry operation closed successfully.");
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        // Log the error that occurred during retry
        logger.error("Error occurred during retry operation: {}", throwable.getMessage(), throwable);
    }
}
