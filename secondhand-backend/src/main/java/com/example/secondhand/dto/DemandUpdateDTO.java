package com.example.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DemandUpdateDTO {

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String expectedCondition;

    private String expectedLocation;

    private String keywords;
}