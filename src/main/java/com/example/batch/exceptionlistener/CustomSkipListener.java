package com.example.batch.exceptionlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSkipListener implements SkipListener<Object, Object> {

    private static final Logger logger = LoggerFactory.getLogger(CustomSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        logger.error("Skipped item in read due to: {}", t.getMessage(), t);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        logger.error("Skipped item in write: {} due to: {}", item, t.getMessage(), t);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        logger.error("Skipped item in process: {} due to: {}", item, t.getMessage(), t);
    }
}
