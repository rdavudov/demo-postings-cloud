package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.CATEGORY_DESCRIPTION;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_TITLE;
import static com.postings.demo.post.builders.PostBuilder.USER_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import static com.postings.demo.post.utility.JacksonUtility.toJson;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postings.demo.post.builders.TestJwtBuilder;
import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.model.Category;
import com.postings.demo.post.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
@Transactional
@SqlMergeMode(MergeMode.MERGE)
public class CategoryIntegrationTests {
	
	@Value("${service.base.uri}")
	private String baseUri ;
	
	@Autowired
	private MockMvc mockMvc ;
	
	@MockBean
	private UserService userService ;
	
	@Value("${jwt.secret}")
	private String secret ;
	
	private TestJwtBuilder jwtBuilder ;
	
	@BeforeEach
	public void setUp() {
		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("admin@admin.com", Set.of("ADMIN"))) ;
		jwtBuilder = new TestJwtBuilder(secret) ;
	}
	
	@Test
	public void givenCategoryWhenIsSavedThenSuccess() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.id", CoreMatchers.any(Integer.class)))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenCategoryWhenIsUpdatedThenSuccess() throws Exception {
		Category category = new Category(0L, "new" + CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.id", is(category.getId().intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.title", is("category a")))
			.andExpect(jsonPath("$.description", is("description a")))
			.andReturn();
	}

	@Test
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenAllWhenIsGetThenSuccess() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(2)))
			.andReturn();
	}
	
	@Test
	public void givenCategoryNotExistWhenIsGetThenNotFound() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	@Sql({"/test-sql/insert_cats.sql"})
	public void givenCategoryWhenIsDeletedThenSuccess() throws Exception {
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andReturn();
	}
	
	@Test
	public void givenMissingCategoryWhenIsDeletedThenNotFound() throws Exception {
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void givenMissingCategoryWhenIsUpdatedThenNotFound() throws Exception {
		Category category = new Category(0L, "new" + CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
}