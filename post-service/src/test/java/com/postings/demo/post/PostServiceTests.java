package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.BODY;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_ID;
import static com.postings.demo.post.builders.PostBuilder.HASHTAGS;
import static com.postings.demo.post.builders.PostBuilder.ID;
import static com.postings.demo.post.builders.PostBuilder.PUBLIC;
import static com.postings.demo.post.builders.PostBuilder.REFERENCE;
import static com.postings.demo.post.builders.PostBuilder.STARS;
import static com.postings.demo.post.builders.PostBuilder.TITLE;
import static com.postings.demo.post.builders.PostBuilder.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.postings.demo.post.builders.PostBuilder;
import com.postings.demo.post.client.UserClient;
import com.postings.demo.post.model.Post;
import com.postings.demo.post.model.User;
import com.postings.demo.post.repository.HashtagRepository;
import com.postings.demo.post.repository.PostRepository;
import com.postings.demo.post.service.PostService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PostServiceTests {

	@Autowired
	private PostService postService ;
	
	@MockBean
	private PostRepository postRepository ;
	
	@MockBean
	private HashtagRepository hashtagRepository ;
	
	@MockBean
	private UserClient userClient ;
	
	@BeforeEach
	public void setUp() {
		when(userClient.getUser(anyString())).thenReturn(Optional.of(new User())) ;
		doNothing().when(hashtagRepository).deleteByPostId(anyLong());
		when(hashtagRepository.save(any())).thenReturn(null);
	}
	
	@Test
	@DisplayName("findById Success")
	public void givenPostIdWhenFindByIdThenSuccess() {
		doReturn(Optional.of(new PostBuilder().sample().build())).when(postRepository).findById(ID) ;
		Optional<Post> post = postService.findById(ID) ;
		assertThat(post).isPresent() ;
		verify(postRepository, times(1)).findById(ID) ;
	}
	
	@Test
	@DisplayName("findById NotFound")
	public void givenNotExistingPostIdWhenFindByIdThenNotFound() {
		doReturn(Optional.empty()).when(postRepository).findById(ID) ;
		Optional<Post> post = postService.findById(ID) ;
		assertThat(post).isEmpty() ;
		verify(postRepository, times(1)).findById(ID) ;
	}
	
	@Test
	@DisplayName("save Success")
	public void givenPostWhenIsSavedThenSuccess() {
		Post post = new PostBuilder().sample().build() ;
		doReturn(post).when(postRepository).save(any()) ;
		ArgumentCaptor<Post> acPost = ArgumentCaptor.forClass(Post.class) ;
		
		Post savedPost = postService.save(post);
		
		assertThat(savedPost).isNotNull() ;
		assertThat(savedPost.getId()).isNotNull().isEqualTo(ID) ;
		assertThat(savedPost.getTitle()).isNotNull().isEqualTo(TITLE) ;
		assertThat(savedPost.getBody()).isNotNull().isEqualTo(BODY) ;
		assertThat(savedPost.getCategory().getId()).isNotNull().isEqualTo(CATEGORY_ID) ;
		assertThat(savedPost.isPublic()).isNotNull().isEqualTo(PUBLIC) ;
		assertThat(savedPost.getStars()).isNotNull().isEqualTo(STARS) ;
		assertThat(savedPost.getReference()).isNotNull().isEqualTo(REFERENCE) ;
		assertThat(savedPost.getHashtags().size()).isNotNull().isEqualTo(HASHTAGS.size());
		assertThat(savedPost.getUserId()).isNotNull().isEqualTo(USER_ID) ;
		verify(postRepository, times(1)).save(any()) ;
		verify(postRepository).save(acPost.capture()) ;
		assertThat(acPost.getValue().getCreatedAt()).isNotNull() ;
	}
	
	@Test
	@DisplayName("update Success")
	public void givenPostWhenIsUpdatedThenSuccess() {
		Post post = new PostBuilder().sample().build() ;
		doReturn(post).when(postRepository).save(any()) ;
		ArgumentCaptor<Post> acPost = ArgumentCaptor.forClass(Post.class) ;
		
		Post updatedPost = postService.update(post);
		
		assertThat(updatedPost).isNotNull() ;
		assertThat(updatedPost.getId()).isNotNull().isEqualTo(ID) ;
		assertThat(updatedPost.getTitle()).isNotNull().isEqualTo(TITLE) ;
		assertThat(updatedPost.getBody()).isNotNull().isEqualTo(BODY) ;
		assertThat(updatedPost.getCategory().getId()).isNotNull().isEqualTo(CATEGORY_ID) ;
		assertThat(updatedPost.isPublic()).isNotNull().isEqualTo(PUBLIC) ;
		assertThat(updatedPost.getStars()).isNotNull().isEqualTo(STARS) ;
		assertThat(updatedPost.getReference()).isNotNull().isEqualTo(REFERENCE) ;
		assertThat(updatedPost.getHashtags().size()).isNotNull().isEqualTo(HASHTAGS.size());
		assertThat(updatedPost.getUserId()).isNotNull().isEqualTo(USER_ID) ;
		verify(postRepository, times(1)).save(any()) ;
		verify(postRepository).save(acPost.capture()) ;
		assertThat(acPost.getValue().getEditedAt()).isNotNull() ;
	}
	
	@Test
	@DisplayName("delete Success")
	public void givenPostIdWhenIsDeletedThenSuccess() {
		doNothing().when(postRepository).deleteById(anyLong());
		
		postService.delete(ID) ;
		
		verify(postRepository).deleteById(ID);
	}
	
	@Test
	@DisplayName("findByUserId Success")
	public void givenPageWhenIsGetThenSuccess() {
		Page<Post> page = new PageImpl<>(new PostBuilder().samples(5)) ;
		when(postRepository.findByUserId(anyString(), any(Pageable.class))).thenReturn(page) ;
		Page<Post> posts = postService.findByUserId(USER_ID, PageRequest.of(0, 5).first()) ;
		assertThat(posts.getContent().size()).isEqualTo(5) ;
	}
	
	@Test
	@DisplayName("findByUserIdOrIsPublic Success")
	public void givenUserIdAndPublicWhenIsGetThenSuccess() {
		List<Post> list = new PostBuilder().samples(5) ;
		when(postRepository.findByUserIdOrIsPublic(anyString(), anyBoolean())).thenReturn(list) ;
		List<Post> posts = postService.findByUserIdOrIsPublic(USER_ID, true) ;
		assertThat(posts.size()).isEqualTo(5) ;
	}
}
