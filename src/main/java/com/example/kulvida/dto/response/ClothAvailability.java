package com.example.kulvida.dto.response;

import com.example.kulvida.entity.cloth.Cloth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ClothAvailability extends ClothDto {

    private String requestedSize;
    private Double requestedPrice;
    private int quantity;

    public ClothAvailability(Cloth cloth) {
        super(cloth);
    }
}
