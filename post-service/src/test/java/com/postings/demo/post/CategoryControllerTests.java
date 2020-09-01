package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.CATEGORY_DESCRIPTION;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_ID;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_TITLE;
import static com.postings.demo.post.builders.PostBuilder.USER_ID;
import static com.postings.demo.post.utility.JacksonUtility.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.postings.demo.post.builders.TestJwtBuilder;
import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.model.Category;
import com.postings.demo.post.service.CategoryService;
import com.postings.demo.post.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
public class CategoryControllerTests {
	
	@Value("${service.base.uri}")
	private String baseUri ;
	
	@Autowired
	private MockMvc mockMvc ;
	
	@MockBean
	private CategoryService categoryService ;
	
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
		
		when(categoryService.save(any())).thenReturn(new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION)) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/categories/" + CATEGORY_ID))
			.andExpect(jsonPath("$.id", is(CATEGORY_ID.intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
	public void givenCategoryWhenIsUpdatedThenSuccess() throws Exception {
		Category category = new Category(0L, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		when(categoryService.save(any())).thenReturn(category) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/categories/" + category.getId()))
			.andExpect(jsonPath("$.id", is(category.getId().intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/categories/" + category.getId()))
			.andExpect(jsonPath("$.id", is(category.getId().intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
	public void givenMissingUserIdWhenIsGetThenValidationError() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0"))
			.andExpect(status().isUnauthorized())
			.andReturn();
	}
	
	@Test
	public void givenMissingUserIdWhenIsDeleteThenValidationError() throws Exception {
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0"))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenMissingAdminRoleWhenIsGetThenValidationError() throws Exception {
		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("test@test.com", Set.of("USER"))) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0").header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenMissingAdminRoleWhenIsDeleteThenValidationError() throws Exception {
		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("test@test.com", Set.of("USER"))) ;
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0").header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	

	
	@Test
	public void givenCategoryNotExistWhenIsGetThenNotFound() throws Exception {
		when(categoryService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWhenIsDeletedThenSuccess() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		doNothing().when(categoryService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andReturn();
	}
	
	@Test
	public void givenMissingCategoryWhenIsDeletedThenNotFound() throws Exception {
		when(categoryService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWhenServiceDeletedThrowsExceptionThenFailure() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		doThrow(RuntimeException.class).when(categoryService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenMissingCategoryWhenIsUpdatedThenNotFound() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		when(categoryService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWhenServiceUpdateThrowsExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		when(categoryService.save(any())).thenThrow(RuntimeException.class) ;		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWhenServiceUpdateReturnsNullExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		when(categoryService.save(any())).thenReturn(null);
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWhenServiceSaveThrowsExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.save(any())).thenThrow(RuntimeException.class) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWhenServiceSaveReturnsNullExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.save(any())).thenReturn(null);
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWithMissingTitleWhenIsSavedThenValidationError() throws Exception {
		Category category = new Category(null, null, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("Authorization", "Bearer " + jwtBuilder.jwt()).contentType(MediaType.APPLICATION_JSON).content(toJson(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWithMissingDescriptionWhenIsSavedThenValidationError() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, null) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("Authorization", "Bearer " + jwtBuilder.jwt()).contentType(MediaType.APPLICATION_JSON).content(toJson(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWithMissingTitleWhenIsUpdatedThenValidationError() throws Exception {
		Category category = new Category(null, null, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("Authorization", "Bearer " + jwtBuilder.jwt()).contentType(MediaType.APPLICATION_JSON).content(toJson(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWithMissingDescriptionWhenIsUpdatedThenValidationError() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, null) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("Authorization", "Bearer " + jwtBuilder.jwt()).contentType(MediaType.APPLICATION_JSON).content(toJson(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenCategoryWithMissingUserIdWhenIsUpdatedThenValidationError() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").contentType(MediaType.APPLICATION_JSON).content(toJson(category)))
			.andExpect(status().isForbidden())
			.andReturn();
	}
}