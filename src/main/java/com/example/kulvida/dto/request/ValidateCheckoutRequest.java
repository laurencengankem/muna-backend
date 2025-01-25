package com.example.kulvida.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateCheckoutRequest {

    List<ValidateCheckoutItem> items=new ArrayList<>();

    private String username;


}


