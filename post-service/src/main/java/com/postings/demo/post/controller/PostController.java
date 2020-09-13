package com.postings.demo.post.controller;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postings.demo.post.dto.PostDto;
import com.postings.demo.post.dto.UserStats;
import com.postings.demo.post.mapper.PostMapper;
import com.postings.demo.post.model.Post;
import com.postings.demo.post.service.PostService;
import com.postings.demo.post.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "${service.base.uri}/posts")
@Slf4j
@RequiredArgsConstructor
public class PostController {

	@Value("${service.base.uri}/posts")
	private String baseUri ;

	@Autowired
	private final PostService postService ;
	
	@Autowired
	private final UserService userService ;

	@Autowired
	private final PostMapper postMapper ;

	@GetMapping("/{id}")
	public ResponseEntity<?> getPost(@PathVariable("id") Long id, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		return postService.findById(id).map(post -> {
			try {
				if (post.isPublic() || post.getUserId().equals(jwt.getToken().getSubject())) {
					return ResponseEntity.ok()
							.location(new URI(baseUri + "/" + id))
							.body(postMapper.toDto(post)) ;
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build() ;
				}
			} catch (Exception e) {
				log.error("error in getting post {}", id, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}
	
	@GetMapping
	public List<PostDto> getPosts(@AuthenticationPrincipal JwtAuthenticationToken jwt, @PageableDefault(page = 0, size = 20) Pageable pageable) {
		Page<Post> postPage = postService.findByUserId(jwt.getToken().getSubject(), pageable) ;
		List<PostDto> dtoList = postPage.getContent().stream().map(p -> postMapper.toDto(p)).collect(Collectors.toList()) ;
		return dtoList ;
	}
	
	@GetMapping("/search")
	public List<PostDto> searchPosts(@RequestParam(name = "text", required = false) String text, 
			@RequestParam(name = "onlyTitle", required = false) Boolean onlyTitle, 
			@RequestParam(name = "categoryId", required = false) Long categoryId, 
			@RequestParam(name = "hashtags", required = false) Set<String> hashtags,
			@AuthenticationPrincipal JwtAuthenticationToken jwt, @PageableDefault(page = 0, size = 20) Pageable pageable) {

		class SearchCondition {
			private String text ;
			private Boolean onlyTitle ;
			private Long categoryId ;
			private Set<String> hashtags ;
			
			public SearchCondition(String text, Boolean onlyTitle, Long categoryId, Set<String> hashtags) {
				this.text = text != null ? text.toLowerCase() : null ;
				this.onlyTitle = onlyTitle != null ? onlyTitle : false ;
				this.categoryId = categoryId;
				this.hashtags = hashtags != null ? hashtags : new HashSet<String>();
			}
			
			private boolean filterByText(Post post) {
				if (text == null) {
					return true ;
				}
				
				if (onlyTitle) {
					return post.getTitle().contains(text) ;
				} else {
					return post.getTitle().contains(text) || post.getBody().contains(text) ;
				}
			}
			
			private boolean filterByCategory(Post post) {
				if (categoryId != null) {
					return post.getCategory().getId().equals(categoryId) ;
				}
				return true ;
			}
			
			private boolean filterByHashtags(Post post) {
				if (hashtags.size() > 0) {
					return post.getHashtags().stream().anyMatch(hashtags::contains) ;
				}
				return true ;
			}
			
			public boolean isFiltered(Post post) {
				return filterByText(post) && filterByCategory(post) && filterByHashtags(post) ;
			}
		}
		
		SearchCondition condition = new SearchCondition(text, onlyTitle, categoryId, hashtags);
		
		List<Post> posts = postService.findByUserIdOrIsPublic(jwt.getToken().getSubject(), true).stream().filter(p -> condition.isFiltered(p)).collect(Collectors.toList()) ;
		List<PostDto> dtoList = posts.stream().map(p -> postMapper.toDto(p)).collect(Collectors.toList()) ;
		List<PostDto> pageDto = dtoList.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).collect(Collectors.toList()) ;
		
		return pageDto ;
	}
	
	@PostMapping
	public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto dto, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		String userId = jwt.getToken().getSubject() ;
		try {
			Post newPost = postMapper.toPost(dto) ;
			newPost.setUserId(userId);
			PostDto createdDto = postMapper.toDto(postService.save(newPost)) ;
			
			int postCount = postService.findCountByUserId(userId) ;
			UserStats stats = UserStats.withPostCount(userId, postCount) ;
			userService.setStats(userId, jwt.getToken().getTokenValue(), stats);
			
			return ResponseEntity
					.created(new URI(baseUri + "/" + createdDto.getId()))
					.body(createdDto);
		} catch (Exception e) {
			log.error("error in creating post", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostDto dto, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		return postService.findById(id).map(post -> {
			try {
				if (post.getUserId().equals(jwt.getToken().getSubject())) {
					postMapper.toPost(dto, post) ;
					Post updated = postService.update(post) ;
					return ResponseEntity.ok()
							.location(new URI(baseUri + "/" + updated.getId()))
							.body(postMapper.toDto(updated)) ;
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build() ;
				}
			} catch (Exception e) {
				log.error("error in updating post {}", id, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		String userId = jwt.getToken().getSubject() ;
		return postService.findById(id).map(post -> {
			try {
				if (post.getUserId().equals(userId)) {
					postService.delete(id) ;
					
					int postCount = postService.findCountByUserId(userId) ;
					UserStats stats = UserStats.withPostCount(userId, postCount) ;
					userService.setStats(userId, jwt.getToken().getTokenValue(), stats);
					
					return ResponseEntity.ok().build();
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build() ;
				}
			} catch (Exception e) {
				log.error("error in deleting post {}", id, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}
}
