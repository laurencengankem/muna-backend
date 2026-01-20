package com.example.kulvida.dto.request;

import com.example.kulvida.entity.cloth.ClothSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeRequest implements Comparable<SizeRequest>{

    private String  name;
    private Integer quantity;
    private String location;
    private Double price;
    private Integer magasin;


    public SizeRequest(ClothSize cs){
        this.name=cs.getSize().getName();
        this.quantity= cs.getQuantity();
        this.price= cs.getPrice();
        this.magasin= cs.getMagasin();
        this.location= cs.getLocation();

    }



    @Override
    public int compareTo(@NotNull SizeRequest o) {
        if(this.getPrice()>o.getPrice())
            return 1;
        else return -1;
    }
}
