package com.example.kulvida.dto.response;

import com.example.kulvida.dto.request.SizeRequest;
import com.example.kulvida.entity.cloth.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ClothDto {

    private Integer id;
    private String name;
    private String description;
    private Double discount=0.0;
    private Sex sex;
    private String brand;
    private String color;
    private String category;
    private String code;
    private List<ClothPicture> pictures;
    private boolean available;
    private List<SizeRequest> sizes=new ArrayList<>();
    private boolean outOfStock=false;
    private Double cost;

    public ClothDto(Cloth cloth){
        id=cloth.getClothId();
        name= cloth.getName();
        description=cloth.getDescription();
        sex= cloth.getSex();
        color= cloth.getColor();
        discount=cloth.getDiscount();
        category= cloth.getCategory().getName();
        pictures=cloth.getPictures();
        available= cloth.getAvailable();
        brand= cloth.getBrand();
        code= cloth.getCode();
        cost = cloth.getCost();
        if(cloth.getClothSizes()!=null){
            for(ClothSize cs: cloth.getClothSizes()){
                SizeRequest srq=new SizeRequest(cs.getSize().getName(),cs.getQuantity(),cs.getLocation(),cs.getPrice(),cs.getMagasin());
                sizes.add(srq);
            }
        }
    }


}
