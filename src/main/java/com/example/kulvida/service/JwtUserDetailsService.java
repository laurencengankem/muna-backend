package com.example.kulvida.service;

import com.example.kulvida.entity.User;
import com.example.kulvida.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		if(user.getStatus()!=null && user.getStatus().equals("INACTIVE")){
			throw new UsernameNotFoundException("User Disabled with username: " + username);
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				user.getAuthorities());
	}
	
	public User save(User user) {
		User us= userRepository.findByUsername(user.getUsername());
		if(us==null) {
			user.setPassword(bcryptEncoder.encode(user.getPassword()));
			return userRepository.save(user);
		}else return null;
	}

	public User getUser(String username){
		return userRepository.findByUsername(username);
	}
}