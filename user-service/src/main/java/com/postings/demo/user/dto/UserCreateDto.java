package com.postings.demo.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.postings.demo.user.model.User;
import com.postings.demo.user.validator.UniqueEmail;
import com.postings.demo.user.validator.UniqueUsername;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateDto {
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
	
	@NotNull
	private Boolean isBlocked ;
	
	@NotNull(message = "missing firstname")
	private String firstName ;
	
	private String lastName ;
	
	private static ObjectWriter jsonWriter = new ObjectMapper().writer() ;
	
	public String toString() {
		try {
			return jsonWriter.writeValueAsString(this) ;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e) ;
		}
	}
}
