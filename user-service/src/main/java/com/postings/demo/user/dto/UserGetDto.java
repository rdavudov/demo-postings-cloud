package com.postings.demo.user.dto;

import com.postings.demo.user.model.UserStats;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserGetDto {
	private String id ;
	private String email ;
	private String firstName ;
	private String lastName ;
	private String picture ;
	private UserStats stats ;
}
