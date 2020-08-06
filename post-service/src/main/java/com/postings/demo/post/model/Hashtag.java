package com.postings.demo.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(HashtagId.class)
public class Hashtag {
	
	@Id
	private String hashtag ;
	
	@Id
	@Column(name = "post_id")
	private Long postId ;
	
	public static Hashtag fromHashtagString(String s) {
		Hashtag hashtag = new Hashtag() ;
		hashtag.setHashtag(s);
		return hashtag ;
	}
}
