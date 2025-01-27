package com.example.kulvida.dto.response;

import com.example.kulvida.dto.request.SizeRequest;
import com.example.kulvida.entity.cloth.Cloth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClothAvailability extends ClothDto {

    private String requestedSize;
    private Double requestedPrice;
    private String location;
    private int quantity;
    private List<SizeRequest> availableSizes= new ArrayList<>();

    public ClothAvailability(Cloth cloth) {
        super(cloth);
    }
}
