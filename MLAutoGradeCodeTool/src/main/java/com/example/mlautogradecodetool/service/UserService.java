package com.example.mlautogradecodetool.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mlautogradecodetool.model.JwtRequest;
import com.example.mlautogradecodetool.model.JwtResponse;
import com.example.mlautogradecodetool.model.User;
import com.example.mlautogradecodetool.repository.UserRepository;
import com.example.mlautogradecodetool.util.JwtUtil;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthenticationManager  authenticationManager;
	
	public User signUp(User user) throws Exception {
		if(userRepository.findByUsername(user.getUsername()).isPresent())
			throw new Exception("User with given name already exists");
		String pwd = user.getPassword();
		String encryptedPwd = passwordEncoder.encode(pwd);
		user.setPassword(encryptedPwd);
		return userRepository.save(user);
	}
	
	
	public JwtResponse signIn(JwtRequest jwtRequest) throws Exception {
		
		try {
			this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),jwtRequest.getPassword()));
		}
		catch(UsernameNotFoundException e) {
			e.printStackTrace();
			throw new Exception("Invalid Credential");
		}
		
		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(jwtRequest.getUsername());
		
		String token = jwtUtil.generateToken(userDetails);
		return  new JwtResponse(token);
	}
}