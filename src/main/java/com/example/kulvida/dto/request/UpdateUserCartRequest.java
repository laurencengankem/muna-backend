package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateUserCartRequest {

    private String  username;
    private String cartData;

}
