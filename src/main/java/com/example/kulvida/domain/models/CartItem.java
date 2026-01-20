package com.example.kulvida.domain.models;

import com.example.kulvida.dto.request.SizeRequest;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CartItem {
    private Integer id;
    private String name;
    private String photo;
    private Double price;
    private Integer quantity;
    private Double discount;
    private Double discounted;
    private Double total;
    private List<SizeRequest> sizes;
    private String requestedSize;
    private boolean available;
}
