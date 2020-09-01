package com.postings.demo.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.postings.demo.user.validator.UniqueEmail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserCreateDto {
	
	@NotNull(message = "missing id")
	private String id ;
	
	@NotNull(message = "missing email")
	@Email
	@UniqueEmail
	private String email ;
	
	@NotNull(message = "missing firstname")
	private String firstName ;
	
	private String lastName ;
	
	private String picture ;
}
