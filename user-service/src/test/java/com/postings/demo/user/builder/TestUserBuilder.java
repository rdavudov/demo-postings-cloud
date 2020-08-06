package com.postings.demo.user.builder;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;
import com.postings.demo.user.model.User.UserBuilder;

public class TestUserBuilder {
	public static final String ID = "testid" ;
	public static final String FIRSTNAME = "firsttest" ;
	public static final String LASTNAME = "lasttest" ;
	public static final String USERNAME = "testuser" ;
	public static final String PASSWORD = "testtest" ;
	public static final String EMAIL = "test@test.com" ;
	public static final int VERSION = 1 ;
	public static final Boolean BLOCKED = true ;
	public static final Boolean UNBLOCKED = false ;
	
	public static final String DTO_LASTNAME = "lastdto" ;
	
	public static User emptyUser() {
		return emptyUserBuilder().build() ;
	}
	
	public static User testUser() {
		return testUserBuilder().build() ;
	}
	
	public static User fullUser() {
		return fullUserBuilder().build() ;
	}
	
	public static UserBuilder testUserBuilder() {
		return emptyUserBuilder()
				.username(USERNAME)
				.password(PASSWORD)
				.email(EMAIL)
				.blocked(UNBLOCKED)
				.firstname(FIRSTNAME)
				.lastname(LASTNAME);
	}
	
	public static UserBuilder fullUserBuilder() {
		return testUserBuilder()
				.id("testid")
				.version(1) ;
	}
	
	public static UserBuilder emptyUserBuilder() {
		return new User.UserBuilder() ;
	}
	
	public static UserUpdateDto dtoUser() {
		return new UserUpdateDto() ;
	}
	
	public static UserCreateDto createDto() {
		UserCreateDto dto = new UserCreateDto();
		dto.setUsername(USERNAME);
		dto.setPassword(PASSWORD);
		dto.setEmail(EMAIL);
		dto.setIsBlocked(UNBLOCKED);
		dto.setFirstName(FIRSTNAME);
		dto.setLastName(LASTNAME);
		return dto ;
	}
}
