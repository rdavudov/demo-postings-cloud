package com.postings.demo.user.model;

import javax.validation.constraints.Min;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "UserStats")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserStats {
	@Id
	private String id ;
	
	@Min(0)
	private Integer posts = 0 ;
	
	@Min(0)
	private Integer followers = 0;
	
	@Min(0)
	private Integer following = 0;
}
