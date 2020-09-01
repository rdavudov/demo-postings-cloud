package com.postings.demo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserStats {
	private String id ;
	private Integer posts = 0 ;
	private Integer followers = 0;
	private Integer following = 0;
}
