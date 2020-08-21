package com.postings.demo.user.dto;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
	private String firstName ;
	
	private String lastName ;
	
	private String picture ;
	
	@Min(value = 0, message = "posts can be less than zero")
	private Integer posts ;
	
	private static ObjectWriter jsonWriter = new ObjectMapper().writer() ;
	
	public String toString() {
		try {
			return jsonWriter.writeValueAsString(this) ;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e) ;
		}
	}
}
