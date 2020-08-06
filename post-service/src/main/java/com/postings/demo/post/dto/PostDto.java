package com.postings.demo.post.dto;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.postings.demo.post.validator.ValidCategory;

import lombok.Data;

@Data
public class PostDto {
	private Long id ;
	
	@NotBlank
	@Length(min = 2, max = 128, message = "Title length must be between 2 and 128")
	private String title ;
	
	@NotBlank
	private String body ;
	
	private List<String> hashtags ;
	
	@NotNull(message = "reference must not be empty")
	private String reference ;
	
	@Min(value = 0, message = "Stars must be greater or equal than 0")
	@Max(value = 5, message = "Stars must be smaller or equal than 5")
	private int stars ;
	
	@ValidCategory
	private Long categoryId ;
	
	private boolean isPublic ;
	
	private String createdAt ;
	
	private String editedAt ;
}
