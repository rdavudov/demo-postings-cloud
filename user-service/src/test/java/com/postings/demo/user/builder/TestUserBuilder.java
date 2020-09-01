package com.postings.demo.user.builder;

import java.util.Set;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;

public class TestUserBuilder {
	public static final String ID = "testid" ;
	public static final String OTHER_ID = "otherid" ;
	public static final String ADMIN_ID = "adminid" ;
	public static final String FIRSTNAME = "firsttest" ;
	public static final String LASTNAME = "lasttest" ;
	public static final String EMAIL = "test@test.com" ;
	public static final String PICTURE = "http://mypic.com" ;
	
	public static final String DTO_LASTNAME = "lastdto" ;
	public static final Set<String> ROLES = Set.of("ROLE1", "ROLE2") ; 
	
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
				.email(EMAIL)
				.firstname(FIRSTNAME)
				.lastname(LASTNAME)
				.picture(PICTURE);
	}
	
	public static UserBuilder fullUserBuilder() {
		return testUserBuilder()
				.id(ID);
	}
	
	public static UserBuilder emptyUserBuilder() {
		return new UserBuilder() ;
	}
	
	public static UserUpdateDto dtoUser() {
		return new UserUpdateDto() ;
	}
	
	public static UserCreateDto createDto() {
		UserCreateDto dto = new UserCreateDto();
		dto.setEmail(EMAIL);
		dto.setFirstName(FIRSTNAME);
		dto.setLastName(LASTNAME);
		dto.setPicture(PICTURE);
		return dto ;
	}
	
	public static class UserBuilder {
		private String id ;
		private String email ;
		private String firstName ;
		private String lastName ;
		private String picture ;
		
		public UserBuilder id(String id) {
			this.id = id ;
			return this ;
		}
		
		public UserBuilder email(String email) {
			this.email = email ;
			return this ;
		}
		
		public UserBuilder firstname(String firstName) {
			this.firstName = firstName ;
			return this ;
		}
		
		public UserBuilder lastname(String lastName) {
			this.lastName = lastName ;
			return this ;
		}
		
		public UserBuilder picture(String picture) {
			this.picture = picture ;
			return this ;
		}
		
		public User build() {
			return new User(id, email, firstName, lastName, picture) ;
		}
	}
}
