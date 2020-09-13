package com.postings.demo.client.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Post {
	private String title ;
	private String body ;
	private List<String> hashtags ;
	private String reference ;
	private int stars ;
	private Long categoryId ;
	private boolean isPublic ;
}
