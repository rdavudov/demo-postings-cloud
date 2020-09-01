package com.postings.demo.post.dto;

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
	private Integer posts ;
	
	public static UserStats withPostCount(String userId, int posts) {
		return new UserStats(userId, posts) ;
	}
}
