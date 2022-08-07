package com.smart.UserConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

public class UserDetaisServiceImpl implements UserDetailsService{
	
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.getUserByUserName(username);
//		System.out.println(user);
		if(user == null) {
			System.out.println("user is not found");
			throw new UsernameNotFoundException("could not found user ....");
		}
		
//		CustomUserDetails customUserDetails = new CustomUserDetails(user);
//		System.out.println(customUserDetails.getUsername());
		
		return new CustomUserDetails(user);
	}

}
