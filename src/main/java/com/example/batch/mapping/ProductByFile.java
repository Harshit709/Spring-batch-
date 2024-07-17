package com.example.batch.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductByFile {
    private Integer id;
    private String title;
    private String description;
    private String price;
    private String discount;
}
