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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;

import com.postings.demo.post.builders.PostBuilder;
import com.postings.demo.post.model.Hashtag;
import com.postings.demo.post.model.Post;
import com.postings.demo.post.repository.PostRepository;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@Transactional
@Sql({"/test-sql/insert_cats.sql"})
// if SqlMergeMode.MERGE doesnt exist then we will have to add insert_cats.sql to every test case
// if it exists it merges above @Sql with test method's
@SqlMergeMode(MergeMode.MERGE)
public class PostRepositoryTests {

	@Autowired
	private PostRepository postRepository ;
	
	@Test
	@DisplayName("findById Success")
	@Sql({"/test-sql/insert1.sql"})
	public void givenPostIdWhenFindByIdThenSuccess() {
		Optional<Post> post = postRepository.findById(ID) ;
		assertThat(post).isPresent() ;
		assertThat(post.get().getId()).isNotNull().isEqualTo(ID) ;
		assertThat(post.get().getTitle()).isNotNull().isEqualTo(TITLE) ;
		assertThat(post.get().getBody()).isNotNull().isEqualTo(BODY) ;
		assertThat(post.get().getCategory().getId()).isNotNull().isEqualTo(CATEGORY_ID) ;
		assertThat(post.get().isPublic()).isNotNull().isEqualTo(PUBLIC) ;
		assertThat(post.get().getStars()).isNotNull().isEqualTo(STARS) ;
		assertThat(post.get().getReference()).isNotNull().isEqualTo(REFERENCE) ;
		assertThat(post.get().getHashtags().size()).isNotNull().isEqualTo(HASHTAGS.size());
		assertThat(post.get().getUserId()).isNotNull().isEqualTo(USER_ID) ;
		assertThat(post.get().getCreatedAt()).isNotNull() ;
	}
	
	@Test
	@DisplayName("findById NotFound")
	@Sql({"/test-sql/insert1.sql"})
	public void givenNotExistingPostIdWhenFindByIdThenNotFound() {
		Optional<Post> post = postRepository.findById(ID + 1) ;
		assertThat(post).isEmpty() ;
	}
	
	@Test
	@DisplayName("save Success")
	public void givenPostWhenIsSavedThenSuccess() {
		Post post = new PostBuilder().sample().id(null).build() ;
		post.setCreatedAt(LocalDateTime.now());
		Post savedPost = postRepository.save(post);
		
		assertThat(savedPost).isNotNull() ;
		assertThat(savedPost.getId()).isNotNull() ;
		assertThat(savedPost.getTitle()).isNotNull().isEqualTo(TITLE) ;
		assertThat(savedPost.getBody()).isNotNull().isEqualTo(BODY) ;
		assertThat(savedPost.getCategory().getId()).isNotNull().isEqualTo(CATEGORY_ID) ;
		assertThat(savedPost.isPublic()).isNotNull().isEqualTo(PUBLIC) ;
		assertThat(savedPost.getStars()).isNotNull().isEqualTo(STARS) ;
		assertThat(savedPost.getReference()).isNotNull().isEqualTo(REFERENCE) ;
		assertThat(savedPost.getHashtags().size()).isNotNull().isEqualTo(HASHTAGS.size());
		assertThat(savedPost.getUserId()).isNotNull().isEqualTo(USER_ID) ;
		assertThat(savedPost.getCreatedAt()).isNotNull() ;
	}
	
	@Test
	@DisplayName("update by adding hashtag Success")
	@Sql({"/test-sql/insert1.sql"})
	public void givenPostWhenIsUpdatedThenSuccess() {
		Post post = new PostBuilder().sample().id(ID).build() ;
		post.setCreatedAt(LocalDateTime.now());
		post.setEditedAt(LocalDateTime.now());
		post.getHashtags().add("t4") ;
		Post savedPost = postRepository.save(post);
		
		assertThat(savedPost).isNotNull() ;
		assertThat(savedPost.getId()).isNotNull() ;
		assertThat(savedPost.getTitle()).isNotNull().isEqualTo(TITLE) ;
		assertThat(savedPost.getBody()).isNotNull().isEqualTo(BODY) ;
		assertThat(savedPost.getCategory().getId()).isNotNull().isEqualTo(CATEGORY_ID) ;
		assertThat(savedPost.isPublic()).isNotNull().isEqualTo(PUBLIC) ;
		assertThat(savedPost.getStars()).isNotNull().isEqualTo(STARS) ;
		assertThat(savedPost.getReference()).isNotNull().isEqualTo(REFERENCE) ;
		assertThat(savedPost.getHashtags().size()).isNotNull().isEqualTo(HASHTAGS.size() + 1);
		assertThat(savedPost.getUserId()).isNotNull().isEqualTo(USER_ID) ;
		assertThat(savedPost.getCreatedAt()).isNotNull() ;
	}
	
	@Test
	@DisplayName("update by removing hashtag Success")
	@Sql({"/test-sql/insert1.sql"})
	public void givenPostHashtagRemovedWhenIsUpdatedThenSuccess() {
		Post post = new PostBuilder().sample().id(ID).build() ;
		post.setCreatedAt(LocalDateTime.now());
		post.setEditedAt(LocalDateTime.now());
		post.setHashtags(List.of("t1", "t2").stream().collect(Collectors.toList()));
		Post savedPost = postRepository.save(post);
		
		assertThat(savedPost).isNotNull() ;
		assertThat(savedPost.getId()).isNotNull() ;
		assertThat(savedPost.getTitle()).isNotNull().isEqualTo(TITLE) ;
		assertThat(savedPost.getBody()).isNotNull().isEqualTo(BODY) ;
		assertThat(savedPost.getCategory().getId()).isNotNull().isEqualTo(CATEGORY_ID) ;
		assertThat(savedPost.isPublic()).isNotNull().isEqualTo(PUBLIC) ;
		assertThat(savedPost.getStars()).isNotNull().isEqualTo(STARS) ;
		assertThat(savedPost.getReference()).isNotNull().isEqualTo(REFERENCE) ;
		assertThat(savedPost.getHashtags().size()).isNotNull().isEqualTo(HASHTAGS.size() - 1);
		assertThat(savedPost.getUserId()).isNotNull().isEqualTo(USER_ID) ;
		assertThat(savedPost.getCreatedAt()).isNotNull() ;
	}
	
	@Test
	@DisplayName("delete Success")
	@Sql({"/test-sql/insert1.sql"})
	public void givenPostIdWhenIsDeletedThenSuccess() {
		postRepository.deleteById(ID) ;
	}
	
	@Test
	@DisplayName("findByUserId Success")
	@Sql({"/test-sql/insert5.sql", "/test-sql/insert_public2.sql"})
	public void givenPageWhenIsGetThenSuccess() {
		Page<Post> posts = postRepository.findByUserId(USER_ID, PageRequest.of(0, 5).first()) ;
		assertThat(posts.getContent().size()).isEqualTo(5) ;
	}
	
	@Test
	@DisplayName("findByUserIdOrIsPublic Success")
	@Sql({"/test-sql/insert5.sql", "/test-sql/insert_public2.sql"})
	public void givenUserIdAndPublicWhenIsGetThenSuccess() {
		List<Post> posts = postRepository.findByUserIdOrIsPublic(USER_ID, true) ;
		assertThat(posts.size()).isEqualTo(7) ;
	}
	
	@Test
	@DisplayName("findCountByUserId Success")
	@Sql({"/test-sql/insert5.sql", "/test-sql/insert_public2.sql"})
	public void givenUserIdWhenIsCountedThenSuccess() {
		int count = postRepository.findCountByUserId(USER_ID) ;
		assertThat(count).isEqualTo(5) ;
	}
}
