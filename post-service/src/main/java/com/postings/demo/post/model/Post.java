package com.postings.demo.post.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import com.postings.demo.post.converter.HashtagConverter;

import lombok.Data;

@Entity
@Data
public class Post {
	
	@Id
	@GeneratedValue
	private Long id ;
	
	@NotBlank
	@Length(min = 2, max = 128, message = "Title length must be between 2 and 128")
	private String title ;
	
	@NotBlank
	private String body ;
	
	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	@NotNull
	private LocalDateTime createdAt ;
	
	@UpdateTimestamp
	@NotNull
	private LocalDateTime editedAt ;
	
	@NotNull
	private String userId ;
	
	@Convert(converter = HashtagConverter.class)
	private List<String> hashtags ;
	
	@NotNull
	private String reference ;
	
	@Min(value = 0, message = "Stars must be greater or equal than 0")
	@Max(value = 5, message = "Stars must be smaller or equal than 5")
	private int stars ;
	
	@ManyToOne
	@NotNull
	private Category category ;
	
	private boolean isPublic ;
}
