package com.example.batch.config;

import com.example.batch.exceptionlistener.CustomRetryListener;
import com.example.batch.exceptionlistener.CustomSkipListener;
import com.example.batch.constants.BatchConstants;
import com.example.batch.proccessor.GenericItemProcessor;
import com.example.batch.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class GenericSpringBatchConfig<T> {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final GenericItemProcessor<T, T> genericItemProcessor;
    private final CustomRetryListener customRetryListener;
    private final CustomSkipListener customSkipListener;
    public final ProductsRepository productsRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<T> reader(
            @Value("#{jobParameters['" + BatchConstants.FILE_PATH_PARAM + "']}") String filePath,
            @Value("#{jobParameters['" + BatchConstants.FIELD_NAMES_PARAM + "']}") String fieldNames,
            @Value("#{jobParameters['" + BatchConstants.TARGET_CLASS_PARAM + "']}") String targetClassName)
            throws ClassNotFoundException {

        Class<T> targetClass = (Class<T>) Class.forName(targetClassName);
        FlatFileItemReader<T> itemReader = new FlatFileItemReader<>();
        itemReader.setName("myReader");
        itemReader.setLinesToSkip(1); // Skip the header line

        itemReader.setResource(new FileSystemResource(filePath));
        itemReader.setLineMapper(lineMapper(fieldNames, targetClass));

        itemReader.setSkippedLinesCallback(line -> System.out.println("Skipped line: " + line));
        itemReader.setSaveState(false); // Ensure reader state is not corrupted
        return itemReader;
    }

    private LineMapper<T> lineMapper(String fieldNames, Class<T> targetClass) {
        DefaultLineMapper<T> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(fieldNames.split(","));
        BeanWrapperFieldSetMapper<T> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(targetClass);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public Step step1() throws ClassNotFoundException {
        return new StepBuilder(BatchConstants.STEP_NAME, jobRepository)
                .<T, T>chunk(10, transactionManager)
                .reader(reader(null, null, null))
                .processor(genericItemProcessor)
                .writer(writer())
                .faultTolerant()
                .retryLimit(4)
                .retry(Exception.class)
                .skipLimit(10)
                .skip(Exception.class)
                .listener(customSkipListener)
                .listener(customRetryListener)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public RepositoryItemWriter<T> writer() {
        RepositoryItemWriter<T> writer = new RepositoryItemWriter<>();
        writer.setRepository((JpaRepository<T, ?>) productsRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Job runJob() throws ClassNotFoundException {
        return new JobBuilder(BatchConstants.JOB_NAME, jobRepository)
                .start(step1())
                .listener(jobCompletionNotificationImpl())
                .build();
    }

    @Bean
    public JobCompletionNotificationImpl jobCompletionNotificationImpl() {
        return new JobCompletionNotificationImpl();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
}
