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
    private Integer discount=0;
    private Sex sex;
    private String brand;
    private String location;
    private String category;
    private String code;
    private List<ClothPicture> pictures;
    private boolean available;
    private List<SizeRequest> sizes=new ArrayList<>();
    private boolean outOfStock=false;

    public ClothDto(Cloth cloth){
        id=cloth.getClothId();
        name= cloth.getName();
        description=cloth.getDescription();
        sex= cloth.getSex();
        discount=cloth.getDiscount();
        category= cloth.getCategory().getName();
        pictures=cloth.getPictures();
        available= cloth.getAvailable();
        brand= cloth.getBrand();
        code= cloth.getCode();
        if(cloth.getClothSizes()!=null){
            for(ClothSize cs: cloth.getClothSizes()){
                SizeRequest srq=new SizeRequest(cs.getSize().getName(),cs.getQuantity(),cs.getLocation(),cs.getPrice());
                sizes.add(srq);
            }
        }
    }


}
