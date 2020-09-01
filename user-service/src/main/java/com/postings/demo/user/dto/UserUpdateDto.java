package com.postings.demo.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserUpdateDto {
	private String firstName ;
	private String lastName ;
	private String picture ;
}
