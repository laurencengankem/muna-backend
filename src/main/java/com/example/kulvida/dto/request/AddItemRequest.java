package com.example.kulvida.dto.request;

import com.example.kulvida.entity.cloth.Sex;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString
public class AddItemRequest {

    private String name;
    private String description;
    private double price;
    private Integer quantity;
    private String category;
    private Sex sex;
    private String brand;
    private String color;
    private String code;
    private List<SizeRequest> sizes;

}
