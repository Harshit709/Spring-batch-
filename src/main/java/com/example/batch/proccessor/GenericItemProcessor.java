package com.example.batch.proccessor;


import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component
public interface GenericItemProcessor<T, R> extends ItemProcessor<T, R> {
}
