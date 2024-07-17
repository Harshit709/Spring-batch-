package com.example.batch.config;

import com.example.batch.interceptor.RepositoryInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationImpl implements JobExecutionListener {

    @Autowired
    public RepositoryInterceptor repositoryInterceptor;

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationImpl.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job starting: {}", jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job finished: {}", jobExecution);

        if (jobExecution.getFailureExceptions().isEmpty()) {
            logger.info("Number of times save method was called: {}", repositoryInterceptor.getSaveCallCount());
        } else {
            jobExecution.getFailureExceptions().forEach(ex ->
                    logger.error("Job failed with exception: ", ex));
        }

        repositoryInterceptor.resetSaveCallCount();
    }
}
