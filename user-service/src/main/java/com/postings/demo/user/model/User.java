package com.postings.demo.user.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.postings.demo.user.validator.UniqueEmail;
import com.postings.demo.user.validator.UniqueUsername;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
	@Id
	private String id ;
	
	@NotNull(message = "missing username")
	@UniqueUsername
	private String username ;
	
	@NotNull(message = "missing password")
	@Length(message = "password length must be between 8 and 128", min = 8, max = 128)
	private String password ;
	
	@NotNull(message = "missing email")
	@Email
	@UniqueEmail
	private String email ;
	
	@Min(message = "version must be equal or greater than 0", value = 0)
	private Integer version ;
	
	@NotNull
	private Boolean isBlocked ;
	
	@NotNull(message = "missing firstname")
	private String firstName ;
	
	private String lastName ;
	
	private Integer posts ;
	
	private static ObjectWriter jsonWriter = new ObjectMapper().writer() ;
	
	public static class UserBuilder {
		private String id ;
		private String username ;
		private String password ;
		private String email ;
		private Integer version ;
		private Boolean isBlocked ;
		private String firstName ;
		private String lastName ;
		private Integer posts ;
		
		public UserBuilder id(String id) {
			this.id = id ;
			return this ;
		}
		
		public UserBuilder username(String username) {
			this.username = username ;
			return this ;
		}
		
		public UserBuilder password(String password) {
			this.password = password ;
			return this ;
		}
		
		public UserBuilder email(String email) {
			this.email = email ;
			return this ;
		}
		
		public UserBuilder version(Integer version) {
			this.version = version ;
			return this ;
		}
		
		public UserBuilder blocked(Boolean isBlocked) {
			this.isBlocked = isBlocked ;
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
		
		public UserBuilder posts(Integer posts) {
			this.posts = posts ;
			return this ;
		}
		
		public User build() {
			return new User(id, username, password, email, version, isBlocked, firstName, lastName, posts) ;
		}
	}
	
	public String toString() {
		try {
			return jsonWriter.writeValueAsString(this) ;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e) ;
		}
	}
}
