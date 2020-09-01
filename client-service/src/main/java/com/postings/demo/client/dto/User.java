package com.postings.demo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class User {
	private String id ;
	private String email ;
	private String firstName ;
	private String lastName ;
	private String picture ;
	private UserStats stats ;
}
