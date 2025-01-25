package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ValidateEmailRequest {

    private String email;
    private Integer code;
}
