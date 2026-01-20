package com.example.kulvida.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig  {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private PasswordEncoder bcryptPasswordEncoder;

	@Autowired
	private UserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;


	@Bean(name="AuthenticationManager")
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).
				userDetailsService(jwtUserDetailsService).
				passwordEncoder(bcryptPasswordEncoder).
				and().
				build();


	}

	@Bean
	public SecurityFilterChain filter(HttpSecurity http, @Qualifier("AuthenticationManager")AuthenticationManager auth) throws Exception{
		http.csrf().disable();
		http.cors().and();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeHttpRequests().antMatchers("/admin/**").hasAuthority("ADMIN");
		http.authorizeHttpRequests().antMatchers("/operator/**").hasAnyAuthority("ADMIN","OPERATOR");
		http.authorizeHttpRequests().antMatchers("/user/**").authenticated();
		http.authorizeHttpRequests().anyRequest().permitAll();
		http.addFilterBefore(jwtRequestFilter,UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}


}