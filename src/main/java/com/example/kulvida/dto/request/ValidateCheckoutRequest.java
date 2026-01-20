package com.example.kulvida.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidateCheckoutRequest {

    List<ValidateCheckoutItem> items=new ArrayList<>();
    List<ValidateCheckoutItem> oldItems=new ArrayList<>();
    private Integer discount;

    private String username;
    private String paymentMethod;

    private float width;
    private float fontSize;

    private boolean updating;
    private String oldOrderId;


}


