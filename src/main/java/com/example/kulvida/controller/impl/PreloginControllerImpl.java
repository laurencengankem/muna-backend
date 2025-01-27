package com.example.kulvida.controller.impl;

import com.example.kulvida.cache.BaseCacheManager;
import com.example.kulvida.controller.PreloginController;
import com.example.kulvida.domain.enums.UserRole;
import com.example.kulvida.dto.request.PasswordChangeRequest;
import com.example.kulvida.dto.request.PasswordResetRequest;
import com.example.kulvida.dto.request.SendEmailRequest;
import com.example.kulvida.dto.request.ValidateEmailRequest;
import com.example.kulvida.entity.User;
import com.example.kulvida.repository.UserRepository;
import com.example.kulvida.utils.EmailSenderUtil;
import com.example.kulvida.service.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@RestController
public class PreloginControllerImpl implements PreloginController {

    @Autowired
    private EmailSenderUtil senderService;

    @Autowired
    private BaseCacheManager baseCacheManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;
    
    @Autowired
	private PasswordEncoder bcryptEncoder;
    
    @Autowired
    private UserRepository userRepo;




    @Override
    public boolean sendEmail(@RequestBody SendEmailRequest request){
        if(!userRepo.existsByUsername(request.getEmail())) {
            log.info("{}", request);
            int max = 999999;
            int min = 100000;

            HashMap<String, Object> data = new HashMap<>();
            int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
            data.put("password", request.getPassword());
            data.put("phone", request.getPhone());
            data.put("firstName", request.getFirstName());
            data.put("lastName", request.getLastName());
            data.put("code", random_int);
            log.info(" saved {} ", data);
            String subject = "Registration confirmation";
            String body = "Il tuo codice di verifica è il seguente " + random_int;
            log.info(" here " + request.getEmail());
            baseCacheManager.put("REGISTRATION_CACHE", request.getEmail(), data);
            senderService.sendEmail(request.getEmail(), subject, body);
            return true;
        }
        return false;

    }

    @Override
    public boolean validateEmail(@RequestBody ValidateEmailRequest request){
        log.info(request.toString());
        int code=0;
        String password=null, phone =null, firstName =null, lastName = null;
      
        Object o=baseCacheManager.get("REGISTRATION_CACHE",request.getEmail());
        log.info("{}",o);
        if(o!=null){
            code=(int)((HashMap<String,Object>)o).get("code");
            password =(String) ((HashMap<String,Object>)o).get("password");
            phone =(String) ((HashMap<String,Object>)o).get("phone");
            firstName =(String) ((HashMap<String,Object>)o).get("firstName");
            lastName=(String) ((HashMap<String,Object>)o).get("lastName");
            log.info(code+"");
            if(code==request.getCode()){
                User user= new User(request.getEmail(),password);
                user.setPhone(phone);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setRole(UserRole.USER);
                user.setCreatedOn(new Date());
                log.info("USER ROLE BEFORE {}", user.getRole());
                userDetailsService.save(user);
                log.info("USER ROLE AFTER {}", user.getRole());
                return true;
            }

        }

        return false;
    }
    
    @Override
    public boolean resetPassword(@RequestBody PasswordResetRequest request) {
    	if(userRepo.existsByUsername(request.getEmail()) && !userRepo.findByUsername(request.getEmail()).getRole().getValue().equalsIgnoreCase("OPERATOR")) {
            int max=999999;
            int min=100000;
            Integer random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
            baseCacheManager.clearCacheEntry("RESET_CACHE", request.getEmail());
    		baseCacheManager.put("RESET_CACHE",request.getEmail(), random_int);
    		senderService.sendEmail(request.getEmail(),"Password reset link","click on the link below to reset your password \n http://localhost:4200/password/reset-link/"+random_int);
    		return true;
    	}
		return false;
    }

    @Override
    public boolean passwordChange(@RequestBody PasswordChangeRequest request) {
    	log.info("{}", request);
    	Integer code;
    	try {
    		code= (int)baseCacheManager.get("RESET_CACHE", request.getEmail());
		} catch (Exception e) {
            log.error(e.getMessage());
			return false;
		}
       
    	if(Objects.equals(request.getCode(), code)) {
    		 User user = userRepo.findByUsername(request.getEmail());
    		 if (user !=null) {
    			 user.setPassword(bcryptEncoder.encode(request.getPassword()));
    			 userRepo.save(user);
                 baseCacheManager.clearCacheEntry("RESET_CACHE", request.getEmail());
    			 return true;
			}
    		
    	}
    	return false;
    	
    }





}
