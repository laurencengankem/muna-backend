package com.example.kulvida.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SendEmailRequest {

    private String email;
    private String password;
    private String phone;
    private String firstName;
    private String lastName;
    private String role;
}
