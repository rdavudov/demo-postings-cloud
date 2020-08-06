package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.CATEGORY_DESCRIPTION;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_ID;
import static com.postings.demo.post.builders.PostBuilder.CATEGORY_TITLE;
import static com.postings.demo.post.builders.PostBuilder.USER_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import javax.ws.rs.core.MediaType;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postings.demo.post.model.Category;
import com.postings.demo.post.service.CategoryService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class CategoryControllerTests {
	
	@Value("${service.base.uri}")
	private String baseUri ;
	
	@Autowired
	private MockMvc mockMvc ;
	
	@MockBean
	private CategoryService categoryService ;
	
	@Test
//	@DisplayName("POST /posts 201")
	public void givenCategoryWhenIsSavedThenSuccess() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.save(any())).thenReturn(new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION)) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/categories/" + CATEGORY_ID))
			.andExpect(jsonPath("$.id", is(CATEGORY_ID.intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts 200")
	public void givenCategoryWhenIsUpdatedThenSuccess() throws Exception {
		Category category = new Category(0L, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		when(categoryService.save(any())).thenReturn(category) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/categories/" + category.getId()))
			.andExpect(jsonPath("$.id", is(category.getId().intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
//	@DisplayName("GET /posts/0 200")
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/categories/" + category.getId()))
			.andExpect(jsonPath("$.id", is(category.getId().intValue())))
			.andExpect(jsonPath("$.title", is(category.getTitle())))
			.andExpect(jsonPath("$.description", is(category.getDescription())))
			.andReturn();
	}
	
	@Test
//	@DisplayName("GET /posts/0 400")
	public void givenMissingUserIdWhenIsGetThenValidationError() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0"))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
//	@DisplayName("DELETE /posts/0 400")
	public void givenMissingUserIdWhenIsDeleteThenValidationError() throws Exception {
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0"))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
//	@DisplayName("GET /posts/0 404")
	public void givenCategoryNotExistWhenIsGetThenNotFound() throws Exception {
		when(categoryService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
//	@DisplayName("DELETE /posts/0 200")
	public void givenCategoryWhenIsDeletedThenSuccess() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		doNothing().when(categoryService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isOk())
			.andReturn();
	}
	
	@Test
//	@DisplayName("DELETE /posts/0 404")
	public void givenMissingCategoryWhenIsDeletedThenNotFound() throws Exception {
		when(categoryService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
//	@DisplayName("DELETE /posts/0 500")
	public void givenCategoryWhenServiceDeletedThrowsExceptionThenFailure() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		doThrow(RuntimeException.class).when(categoryService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts/0 404")
	public void givenMissingCategoryWhenIsUpdatedThenNotFound() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		when(categoryService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts/0 500")
	public void givenCategoryWhenServiceUpdateThrowsExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		when(categoryService.save(any())).thenThrow(RuntimeException.class) ;		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts/0 500")
	public void givenCategoryWhenServiceUpdateReturnsNullExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.findById(anyLong())).thenReturn(Optional.of(category)) ;
		when(categoryService.save(any())).thenReturn(null);
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
//	@DisplayName("POST /posts 500")
	public void givenCategoryWhenServiceSaveThrowsExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.save(any())).thenThrow(RuntimeException.class) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
//	@DisplayName("POST /posts 500")
	public void givenCategoryWhenServiceSaveReturnsNullExceptionThenFailure() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		when(categoryService.save(any())).thenReturn(null);
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
//	@DisplayName("POST /posts 400")
	public void givenCategoryWithMissingTitleWhenIsSavedThenValidationError() throws Exception {
		Category category = new Category(null, null, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
//	@DisplayName("POST /posts 400")
	public void givenCategoryWithMissingDescriptionWhenIsSavedThenValidationError() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, null) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/categories").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts/0 400")
	public void givenCategoryWithMissingTitleWhenIsUpdatedThenValidationError() throws Exception {
		Category category = new Category(null, null, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts/0 400")
	public void givenCategoryWithMissingDescriptionWhenIsUpdatedThenValidationError() throws Exception {
		Category category = new Category(null, CATEGORY_TITLE, null) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").header("user-id", String.valueOf(USER_ID)).contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
//	@DisplayName("PUT /posts/0 400")
	public void givenCategoryWithMissingUserIdWhenIsUpdatedThenValidationError() throws Exception {
		Category category = new Category(CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESCRIPTION) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/categories/0").contentType(MediaType.APPLICATION_JSON).content(objectAsJsonString(category)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	public static String objectAsJsonString(Object object) throws Exception {
		return new ObjectMapper().writeValueAsString(object) ;
	}
}