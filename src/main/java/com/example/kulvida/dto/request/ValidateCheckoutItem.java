package com.example.kulvida.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateCheckoutItem {

    private Integer id;
    private String code;
    private String name;
    private Integer quantity;
    private Double price;
    private String size;
}
