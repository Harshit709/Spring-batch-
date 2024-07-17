package com.example.batch.interceptor;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryInterceptor {


    private int saveCallCount = 0;

    @Before("execution(* com.example.batch.repository.ProductsRepository.save(..))")
    public void beforeSave() {
        saveCallCount++;
    }

    public int getSaveCallCount() {
        return saveCallCount;
    }

    public void resetSaveCallCount() {
        this.saveCallCount = 0;
    }
}
