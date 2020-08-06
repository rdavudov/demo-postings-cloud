package com.postings.demo.post.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.postings.demo.post.dto.PostDto;
import com.postings.demo.post.model.Category;
import com.postings.demo.post.model.Post;

public class PostBuilder {
	public static final Long ID = 0L ;
	public static final String TITLE = "title" ;
	public static final String BODY = "body";
	public static final Long CATEGORY_ID = 0L;
	public static final String CATEGORY_TITLE = "title" ;
	public static final String CATEGORY_DESCRIPTION = "description";
	public static final Boolean PUBLIC = false ;
	public static final Integer STARS = 3;
	public static final String REFERENCE = "http://www.google.com" ;
	public static final List<String> HASHTAGS = new ArrayList<String>(List.of("t1", "t2", "t3"));
	public static final String USER_ID = "12345";
	public static final String OTHER_USER_ID = "23456";
	
	private Post post = new Post() ;
	
	public PostBuilder id(Long id) {
		post.setId(id);
		return this ;
	}
	
	public PostBuilder title(String title) {
		post.setTitle(title);
		return this ;
	}
	
	public PostBuilder body(String body) {
		post.setBody(body);
		return this ;
	}
	
	public PostBuilder category(Category category) {
		post.setCategory(category);
		return this ;
	}
	
	public PostBuilder category(Long categoryId) {
		post.setCategory(new Category(categoryId, null, null));
		return this ;
	}
	
	public PostBuilder stars(Integer stars) {
		post.setStars(stars);
		return this ;
	}
	
	public PostBuilder reference(String reference) {
		post.setReference(reference);
		return this ;
	}
	
	public PostBuilder userId(String userId) {
		post.setUserId(userId);
		return this ;
	}
	
	public PostBuilder hashtags(List<String> hashtags) {
		post.setHashtags(hashtags);
		return this ;
	}
	
	public PostBuilder isPublic(boolean isPublic) {
		post.setPublic(isPublic);
		return this ;
	}
 	
	public PostBuilder fromDto(PostDto dto) {
		post.setTitle(dto.getTitle());
		post.setBody(dto.getBody());
		post.setPublic(dto.isPublic());
		post.setCategory(new Category(dto.getCategoryId(), null, null));
		post.setStars(dto.getStars());
		post.setReference(dto.getReference());
		post.setUserId(USER_ID);
		if (dto.getHashtags() != null) {
			post.setHashtags(dto.getHashtags());
		}
		return this ;
	}
	
	public Post build() {
		return post ;
	}
	
	public PostBuilder sample() {
		post = new Post() ;
		post.setId(ID);
		post.setTitle(TITLE);
		post.setBody(BODY);
		post.setCategory(new Category(CATEGORY_ID, null, null));
		post.setPublic(PUBLIC);
		post.setStars(STARS);
		post.setReference(REFERENCE);
		post.setHashtags(new ArrayList<String>(HASHTAGS));
		post.setUserId(USER_ID);

		return this ;
	}
	
	public List<Post> samples(int count) {
		return IntStream.range(0, count).mapToObj(id -> {
			Post post = new Post() ;
			post.setId((long) id);
			post.setTitle(TITLE);
			post.setBody(BODY);
			post.setCategory(new Category(CATEGORY_ID, null, null));
			post.setPublic(PUBLIC);
			post.setStars(STARS);
			post.setReference(REFERENCE);
			post.setHashtags(new ArrayList<String>(HASHTAGS));
			post.setUserId(USER_ID);
			return post ;
		}).collect(Collectors.toList()) ;
	}
}
