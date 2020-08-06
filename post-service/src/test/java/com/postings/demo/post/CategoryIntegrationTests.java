package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.CATEGORY_DESCRIPTION;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_TITLE;
import static com.postings.demo.post.builders.PostBuilder.USER_ID;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postings.demo.post.model.Category;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@SqlMergeMode(MergeMode.MERGE)
public class CategoryIntegrationTests {
	
	@Value("${service.base.uri}")
	private String baseUri ;
	
	@Autowired
	private MockMvc mockMvc ;
	
	@Test
//	@DisplayName("POST /posts 201")
	public void givenCategoryWhenIsSavedThenSuccess() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", any(Integer.class)))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts 200")
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenCategoryWhenIsUpdatedThenSuccess() throws Exception {
		Category category = new Category(0L, "new" + CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", is(category.getId().intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
//	@DisplayName("GET /posts/0 200")
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.title", is("category a")))
			.andExpect(jsonPath("$.description", is("description a")))
			.andReturn();
	}

	@Test
	@Sql({"/test-sql/insert_cats.sql"})
//	@DisplayName("GET /posts/0 200")
	public void givenAllWhenIsGetThenSuccess() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(2)))
			.andReturn();
	}
	
	@Test
//	@DisplayName("GET /posts/0 404")
	public void givenCategoryNotExistWhenIsGetThenNotFound() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
//	@DisplayName("DELETE /posts/0 200")
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenCategoryWhenIsDeletedThenSuccess() throws Exception {
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andReturn();
	}
	
	@Test
//	@DisplayName("DELETE /posts/0 404")
	public void givenMissingCategoryWhenIsDeletedThenNotFound() throws Exception {
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts/0 404")
	public void givenMissingCategoryWhenIsUpdatedThenNotFound() throws Exception {
		Category category = new Category(0L, "new" + CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	public static String objectAsJsonString(Object object) throws Exception {
		return new ObjectMapper().writeValueAsString(object) ;
	}
}