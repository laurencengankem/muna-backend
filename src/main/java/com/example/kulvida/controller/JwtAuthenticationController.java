package com.example.kulvida.controller;

import com.example.kulvida.dto.request.JwtRequest;
import com.example.kulvida.dto.request.ValidateTokenRequest;
import com.example.kulvida.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


public interface JwtAuthenticationController {
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @CrossOrigin
    ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception;

    @RequestMapping(value = "/validateToken", method = RequestMethod.POST)
    @CrossOrigin
    ResponseEntity<?> validateToken(@RequestBody ValidateTokenRequest request);

}
