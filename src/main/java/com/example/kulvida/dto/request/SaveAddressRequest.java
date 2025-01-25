package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SaveAddressRequest {

    private Integer id;
    private String user;
    private String country;
    private String city;
    private Integer zip;
    private String street;
}
