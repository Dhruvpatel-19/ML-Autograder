package com.example.mlautogradecodetool.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.mlautogradecodetool.service.CustomUserDetailsService;
import com.example.mlautogradecodetool.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//get jwt
		//Bearer
		//validate
		String requestTokenHeader =  request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;
		
		
		//null and format
		if(requestTokenHeader!=null && (requestTokenHeader.startsWith("Bearer "))){
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = this.jwtUtil.extractUsername(jwtToken);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
			//security
			if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
			else {
				System.out.println("Token is not validated..");
			}
		}
		filterChain.doFilter(request, response);	
	}
}