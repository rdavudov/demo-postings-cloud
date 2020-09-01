package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.BODY;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_ID;
import static com.postings.demo.post.builders.PostBuilder.ID;
import static com.postings.demo.post.builders.PostBuilder.OTHER_USER_ID;
import static com.postings.demo.post.builders.PostBuilder.PUBLIC;
import static com.postings.demo.post.builders.PostBuilder.REFERENCE;
import static com.postings.demo.post.builders.PostBuilder.STARS;
import static com.postings.demo.post.builders.PostBuilder.TITLE;
import static com.postings.demo.post.builders.PostBuilder.USER_ID;
import static com.postings.demo.post.utility.JacksonUtility.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.postings.demo.post.builders.PostBuilder;
import com.postings.demo.post.builders.PostDtoBuilder;
import com.postings.demo.post.builders.TestJwtBuilder;
import com.postings.demo.post.dto.PostDto;
import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.dto.UserStats;
import com.postings.demo.post.model.Hashtag;
import com.postings.demo.post.model.Post;
import com.postings.demo.post.repository.HashtagRepository;
import com.postings.demo.post.service.PostService;
import com.postings.demo.post.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
@Transactional
@Sql({"/test-sql/insert_cats.sql"})
@SqlMergeMode(MergeMode.MERGE)
public class PostIntegrationTests {
	@Value("${service.base.uri}")
	private String baseUri ;
	
	@Autowired
	private MockMvc mockMvc ;
	
	@Autowired
	private HashtagRepository hashtagRepository ;
	
	@SpyBean
	private PostService postService ; 
	
	private WireMockServer server ;
	
	@SpyBean
	private UserService userService ;
	
	@Value("${jwt.secret}")
	private String secret ;
	
	private TestJwtBuilder jwtBuilder ;
	
	@BeforeEach
	public void beforeEach() {
		server = new WireMockServer(9999) ;
		server.start(); 
//		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("test@test.com", Set.of("USER"))) ;
		jwtBuilder = new TestJwtBuilder(secret) ;
	}
	
	@AfterEach
	public void afterEach() {
		server.stop(); 
	}
	
	@Test
	public void givenPostWhenIsSavedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		ArgumentCaptor<UserStats> acStats = ArgumentCaptor.forClass(UserStats.class);
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.id", CoreMatchers.any(Integer.class)))
			.andExpect(jsonPath("$.title", is(dto.getTitle())))
			.andExpect(jsonPath("$.body", is(dto.getBody())))
			.andExpect(jsonPath("$.categoryId", is(dto.getCategoryId().intValue())))
			.andExpect(jsonPath("$.public", is(dto.isPublic())))
			.andExpect(jsonPath("$.stars", is(dto.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andReturn();
		
		verify(postService, times(1)).findCountByUserId(anyString()) ;
		verify(userService, times(1)).setStats(anyString(), anyString(), acStats.capture());
		assertThat(acStats.getValue().getPosts()).isEqualTo(1) ;
	}	
	
	@Test
	@Sql({"/test-sql/insert1.sql"})
	public void givenPostWhenIsSavedThenTotalCountIncreases() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		ArgumentCaptor<UserStats> acStats = ArgumentCaptor.forClass(UserStats.class);
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.id", CoreMatchers.any(Integer.class)))
			.andExpect(jsonPath("$.title", is(dto.getTitle())))
			.andExpect(jsonPath("$.body", is(dto.getBody())))
			.andExpect(jsonPath("$.categoryId", is(dto.getCategoryId().intValue())))
			.andExpect(jsonPath("$.public", is(dto.isPublic())))
			.andExpect(jsonPath("$.stars", is(dto.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andReturn();
		
		verify(postService, times(1)).findCountByUserId(anyString()) ;
		verify(userService, times(1)).setStats(anyString(), anyString(), acStats.capture());
		assertThat(acStats.getValue().getPosts()).isEqualTo(2) ;
		
	}	
	
	@Test
	public void givenPostWithNotExistentUserWhenIsSavedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.id", CoreMatchers.any(Integer.class)))
			.andExpect(jsonPath("$.title", is(dto.getTitle())))
			.andExpect(jsonPath("$.body", is(dto.getBody())))
			.andExpect(jsonPath("$.categoryId", is(dto.getCategoryId().intValue())))
			.andExpect(jsonPath("$.public", is(dto.isPublic())))
			.andExpect(jsonPath("$.stars", is(dto.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andReturn();
		
		verify(userService, times(1)).setStats(anyString(), anyString(), any());
	}
	
	@Test
	@Sql({"/test-sql/insert1.sql"})
	public void givenPostWhenIsUpdatedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().title("new" + TITLE).build() ;
		dto.setHashtags(List.of("t1", "t4"));
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.id", is(ID.intValue())))
			.andExpect(jsonPath("$.title", is(dto.getTitle())))
			.andExpect(jsonPath("$.body", is(dto.getBody())))
			.andExpect(jsonPath("$.categoryId", is(dto.getCategoryId().intValue())))
			.andExpect(jsonPath("$.public", is(dto.isPublic())))
			.andExpect(jsonPath("$.stars", is(dto.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t4")))
			.andExpect(jsonPath("$.reference", is(dto.getReference())))
			.andReturn();
		
		List<Hashtag> hashtags = hashtagRepository.findByPostId(ID) ;
		assertThat(hashtags.size()).isEqualTo(2) ;
	}
	
	@Test
	@Sql({"/test-sql/insert1.sql"})
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.id", is(ID.intValue())))
			.andExpect(jsonPath("$.title", is(TITLE)))
			.andExpect(jsonPath("$.body", is(BODY)))
			.andExpect(jsonPath("$.categoryId", is(CATEGORY_ID.intValue())))
			.andExpect(jsonPath("$.public", is(PUBLIC)))
			.andExpect(jsonPath("$.stars", is(STARS)))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andExpect(jsonPath("$.reference", is(REFERENCE)))
			.andReturn();
	}
	
	@Test
	@Sql({"/test-sql/insert5.sql"})
	public void givenSearchByOnlyTitleWhenIsGetThenSuccess() throws Exception {
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search")
				.param("text", "title2")
				.param("onlyTitle", "true")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$..content.length()", hasItems(1)))
			.andReturn();
	}
	
	@Test
	@Sql({"/test-sql/insert5.sql"})
	public void givenSearchByTitleAndBodyWhenIsGetThenSuccess() throws Exception {
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search")
				.param("text", "body2")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$..content.length()", hasItems(1)))
			.andReturn();
	}
	
	@Test
	@Sql({"/test-sql/insert5.sql"})
	public void givenPostWhenIsDeletedThenSuccess() throws Exception {
		ArgumentCaptor<UserStats> acStats = ArgumentCaptor.forClass(UserStats.class);
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andReturn();
		
		verify(postService, times(1)).findCountByUserId(anyString()) ;
		verify(userService, times(1)).setStats(anyString(), anyString(), acStats.capture());
		assertThat(acStats.getValue().getPosts()).isEqualTo(4) ;
	}
}
