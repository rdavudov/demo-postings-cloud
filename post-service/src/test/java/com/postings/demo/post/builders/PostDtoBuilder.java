package com.postings.demo.post.builders;

import static com.postings.demo.post.builders.PostBuilder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.postings.demo.post.dto.PostDto;
import com.postings.demo.post.model.Post;

public class PostDtoBuilder {
	private PostDto dto = new PostDto() ;
	
	public PostDtoBuilder title(String title) {
		dto.setTitle(title);
		return this ;
	}
	
	public PostDtoBuilder body(String body) {
		dto.setBody(body);
		return this ;
	}
	
	public PostDtoBuilder category(long categoryId) {
		dto.setCategoryId(categoryId);
		return this ;
	}
	
	public PostDtoBuilder stars(int stars) {
		dto.setStars(stars);
		return this ;
	}
	
	public PostDtoBuilder reference(String reference) {
		dto.setReference(reference);
		return this ;
	}
	
	public PostDtoBuilder id(long id) {
		dto.setId(id);
		return this ;
	}
	
	public PostDtoBuilder hashtags(List<String> hashtags) {
		dto.setHashtags(hashtags);
		return this ;
	}
	
	public PostDtoBuilder isPublic(boolean isPublic) {
		dto.setPublic(isPublic);
		return this ;
	}
	
	public PostDtoBuilder fromDto(Post post) {
		dto.setTitle(post.getTitle());
		dto.setBody(post.getBody());
		dto.setPublic(post.isPublic());
		dto.setCategoryId(post.getCategory().getId());
		dto.setStars(post.getStars());
		dto.setReference(post.getReference());
		dto.setHashtags(post.getHashtags());
		return this ;
	}
	
	public PostDto build() {
		return dto ;
	}
	
	public PostDtoBuilder sample() {
		dto = new PostDto() ;
		dto.setTitle(TITLE);
		dto.setBody(BODY);
		dto.setCategoryId(CATEGORY_ID);
		dto.setPublic(PUBLIC);
		dto.setStars(STARS);
		dto.setReference(REFERENCE);
		dto.setHashtags((new ArrayList<String>(HASHTAGS)));

		return this ;
	}
}
