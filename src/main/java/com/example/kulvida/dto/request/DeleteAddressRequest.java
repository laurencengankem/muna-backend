package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeleteAddressRequest {

    private String username;
    private Integer addressId;
}
