package com.postings.demo.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class User {
	private String id ;
	private Integer version ;
	private Integer posts ;
}
