package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderDetailsRequest {

    private String orderId;
    private String username;
}
