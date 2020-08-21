package com.postings.demo.user.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.postings.demo.user.validator.UniqueEmail;

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
	
	@NotNull(message = "missing email")
	@Email
	@UniqueEmail
	private String email ;
	
	@NotNull(message = "missing firstname")
	private String firstName ;
	
	private String lastName ;
	
	private String picture ;
	
	private Integer posts ;
	
	private static ObjectWriter jsonWriter = new ObjectMapper().writer() ;
	
	public static class UserBuilder {
		private String id ;
		private String email ;
		private String firstName ;
		private String lastName ;
		private Integer posts ;
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
		
		public UserBuilder posts(Integer posts) {
			this.posts = posts ;
			return this ;
		}
		
		public UserBuilder picture(String picture) {
			this.picture = picture ;
			return this ;
		}
		
		public User build() {
			return new User(id, email, firstName, lastName, picture, posts) ;
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
