package com.example.kulvida.dto.request;

import lombok.Data;

@Data
public class PasswordChangeRequest {
	
	private String password;
	private String email;
	private Integer code;
	

}
