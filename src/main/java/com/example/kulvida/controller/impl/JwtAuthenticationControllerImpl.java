package com.example.kulvida.controller.impl;

import com.example.kulvida.controller.JwtAuthenticationController;
import com.example.kulvida.dto.request.JwtRequest;
import com.example.kulvida.dto.response.JwtResponse;
import com.example.kulvida.dto.request.ValidateTokenRequest;
import com.example.kulvida.entity.User;
import com.example.kulvida.utils.JwtTokenUtil;
import com.example.kulvida.service.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class JwtAuthenticationControllerImpl implements JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Override
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {


		boolean b=authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		String token=null;
		String role=null;
		if(b){
			final UserDetails userDetails = userDetailsService
					.loadUserByUsername(authenticationRequest.getUsername());

			token = jwtTokenUtil.generateToken(userDetails);
			GrantedAuthority auth= userDetails.getAuthorities().stream().findFirst().orElse(null);
			role= auth==null ? null : auth.getAuthority();
			log.info("TOKEN JWT: {}", token);
			log.info("TOKEN JWT: {}", new JwtResponse(token,role));
		}


		return ResponseEntity.ok(new JwtResponse(token,role));
	}

	
	@Override
	public ResponseEntity<?> validateToken(@RequestBody ValidateTokenRequest request){
		log.info("Token {}", request.getToken());
		try {
			return ResponseEntity.ok(jwtTokenUtil.validateToken(request.getToken()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity(true, HttpStatus.UNAUTHORIZED);
		}
		
	}

	private boolean authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			return true;
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			return false;
		}
	}
}