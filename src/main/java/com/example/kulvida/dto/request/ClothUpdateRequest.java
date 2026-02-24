package com.example.kulvida.dto.request;

import com.example.kulvida.entity.cloth.Sex;
import lombok.Data;

import java.util.List;

@Data
public class ClothUpdateRequest {

    private String name;
    private String description;
    private Double discount;
    private Double cost;
    private String category;
    private String brand;
    private String color;
    private String code;
    private Sex sex;
    private List<SizeRequest> sizes;
    private Boolean available;

}
