package com.postings.demo.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.postings.demo.user.validator.UniqueEmail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateDto {
	@NotNull(message = "missing email")
	@Email
	@UniqueEmail
	private String email ;
	
	@NotNull(message = "missing firstname")
	private String firstName ;
	
	private String lastName ;
	
	private String picture ;
	
	private static ObjectWriter jsonWriter = new ObjectMapper().writer() ;
	
	public String toString() {
		try {
			return jsonWriter.writeValueAsString(this) ;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e) ;
		}
	}
}
