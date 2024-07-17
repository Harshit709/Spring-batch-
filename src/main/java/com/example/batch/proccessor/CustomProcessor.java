package com.example.batch.proccessor;

import com.example.batch.entity.Products;

import com.example.batch.exception.BatchProcessingException;
import com.example.batch.mapping.ProductByFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomProcessor<T, O> implements GenericItemProcessor<T, O> {

    private static final Logger logger = LoggerFactory.getLogger(CustomProcessor.class);


    @Override
    public O process(T item) throws Exception {
        try {
            if (item instanceof ProductByFile) {
                String priceStr = ((ProductByFile) item).getPrice();
                String discountStr = ((ProductByFile) item).getDiscount();

                double price;
                double discount;

                price = Double.parseDouble(priceStr);
                discount = Double.parseDouble(discountStr);

                double discountedPrice = price - (price * discount / 100);

                Products product = new Products();
                product.setId(((ProductByFile) item).getId());
                product.setTitle(((ProductByFile) item).getTitle());
                product.setDescription(((ProductByFile) item).getDescription());
                product.setPrice(priceStr);
                product.setDiscount(discountStr);
                product.setDiscountedPrice(String.format("%.2f", discountedPrice));

                return (O) product;
            }
            return (O) item;
        } catch (Exception e) {
            throw new BatchProcessingException("Error processing item: " + item, e);
        }
    }
}
