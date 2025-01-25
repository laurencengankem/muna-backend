package com.example.kulvida.security;

import com.example.kulvida.utils.JwtTokenUtil;
import com.example.kulvida.service.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");
		final String uri = request.getRequestURI();
		MDC.put("uri", uri);
		log.info("Request URI: {}", uri);
		log.info("Token found: {}", requestTokenHeader);
		String username = null;
		String jwtToken = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				log.error("Unable to get JWT Token: {}", e.getMessage());
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to get JWT Token");
				return; // Prevent further processing
			} catch (Exception e) {
				log.error("JWT processing error: {}", e.getMessage());
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JWT processing error");
				return; // Prevent further processing
			}
		} else {
			log.warn("JWT Token does not begin with Bearer String");
		}

		// Once we get the token validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			try {
				if (jwtTokenUtil.validateUsernameAndToken(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					log.info("User authorities: {}", usernamePasswordAuthenticationToken.getAuthorities());

					// After setting the Authentication in the context, we specify
					// that the current user is authenticated.
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			} catch (Exception e) {
				log.error("Token validation error: {}", e.getMessage());
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
				return; // Prevent further processing
			}
		}

		// Continue the filter chain
		chain.doFilter(request, response);
	}


}