package com.postings.demo.user.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.postings.demo.user.validator.UniqueEmail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
}
