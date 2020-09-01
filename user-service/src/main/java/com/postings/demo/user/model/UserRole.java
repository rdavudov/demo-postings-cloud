package com.postings.demo.user.model;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "UserRoles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRole {
	@Id
	@NotNull
	private String email ;
	
	@NotEmpty
	private Set<String> roles ;
}
