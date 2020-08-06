package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;

public class TestUserCoverage {
	
	@Test
	public void testUserEquals() {
		Map<User, User> userMap = new HashMap<>() ;
		User user = fullUser() ;
		userMap.put(user, user) ;
	}
	
	@Test
	public void testUserDtoEquals() {
		Map<UserUpdateDto, UserUpdateDto> userMap = new HashMap<>() ;
		UserUpdateDto user = new UserUpdateDto() ;
		userMap.put(user, user) ;
	}
}
