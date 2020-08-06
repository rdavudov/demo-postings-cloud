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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.postings.demo.post.builders.PostDtoBuilder;
import com.postings.demo.post.client.UserClient;
import com.postings.demo.post.dto.PostDto;
import com.postings.demo.post.model.Hashtag;
import com.postings.demo.post.model.User;
import com.postings.demo.post.repository.HashtagRepository;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
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
	
	private WireMockServer server ;
	
	@BeforeEach
	public void beforeEach() {
		server = new WireMockServer(9999) ;
		server.start(); 
	}
	
	@AfterEach
	public void afterEach() {
		server.stop(); 
	}
	
	@Test
	public void givenPostWhenIsSavedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(dto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", any(Integer.class)))
			.andExpect(jsonPath("$.title", is(dto.getTitle())))
			.andExpect(jsonPath("$.body", is(dto.getBody())))
			.andExpect(jsonPath("$.categoryId", is(dto.getCategoryId().intValue())))
			.andExpect(jsonPath("$.public", is(dto.isPublic())))
			.andExpect(jsonPath("$.stars", is(dto.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andReturn();
		
//		verify(userClient, times(1)).getUser(anyString()) ;
//		verify(userClient, times(1)).updateUser(anyString(), org.mockito.ArgumentMatchers.any(User.class));
	}	
	
	@Test
	public void givenPostWithNotExistentUserWhenIsSavedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts").header("user-id", OTHER_USER_ID).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(dto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", any(Integer.class)))
			.andExpect(jsonPath("$.title", is(dto.getTitle())))
			.andExpect(jsonPath("$.body", is(dto.getBody())))
			.andExpect(jsonPath("$.categoryId", is(dto.getCategoryId().intValue())))
			.andExpect(jsonPath("$.public", is(dto.isPublic())))
			.andExpect(jsonPath("$.stars", is(dto.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andReturn();
		
//		verify(userClient, times(1)).getUser(anyString()) ;
//		verify(userClient, never()).updateUser(anyString(), org.mockito.ArgumentMatchers.any(User.class));
	}
	
	@Test
	@Sql({"/test-sql/insert1.sql"})
//	@DisplayName("PUT /posts 200")
	public void givenPostWhenIsUpdatedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().title("new" + TITLE).build() ;
		dto.setHashtags(List.of("t1", "t4"));
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(dto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
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
//	@DisplayName("GET /posts/0 200")
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
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
//	@DisplayName("GET /posts/search 200")
	public void givenSearchByOnlyTitleWhenIsGetThenSuccess() throws Exception {
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search").param("text", "title2").param("onlyTitle", "true")
				.header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$..content.length()", hasItems(1)))
			.andReturn();
	}
	
	@Test
	@Sql({"/test-sql/insert5.sql"})
//	@DisplayName("GET /posts/search 200")
	public void givenSearchByTitleAndBodyWhenIsGetThenSuccess() throws Exception {
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search").param("text", "body2")
				.header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$..content.length()", hasItems(1)))
			.andReturn();
	}
	
	public static String objectAsJsonString(Object object) throws Exception {
		return new ObjectMapper().writeValueAsString(object) ;
	}
}
