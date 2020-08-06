package com.postings.demo.post.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashtagId implements Serializable {
	private String hashtag ;
	private Long postId ;
}
