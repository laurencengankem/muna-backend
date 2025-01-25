package com.example.kulvida.controller;

import com.example.kulvida.dto.request.PasswordChangeRequest;
import com.example.kulvida.dto.request.PasswordResetRequest;
import com.example.kulvida.dto.request.SendEmailRequest;
import com.example.kulvida.dto.request.ValidateEmailRequest;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/prelogin")
public interface PreloginController {

    @PostMapping("/email/send")
    @CrossOrigin
    boolean sendEmail(@RequestBody SendEmailRequest request);

    @PostMapping("/email/validation")
    @CrossOrigin
    boolean validateEmail(@RequestBody ValidateEmailRequest request);

    @PostMapping("/passwordReset")
    @CrossOrigin
    boolean resetPassword(@RequestBody PasswordResetRequest request);

    @PostMapping("/passwordChange")
    @CrossOrigin
    boolean passwordChange(@RequestBody PasswordChangeRequest request);
}
