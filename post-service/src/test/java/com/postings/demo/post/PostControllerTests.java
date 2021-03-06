package com.postings.demo.post;

import static com.postings.demo.post.builders.PostBuilder.CATEGORY_ID;
import static com.postings.demo.post.builders.PostBuilder.USER_ID;
import static com.postings.demo.post.utility.JacksonUtility.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.postings.demo.post.builders.PostBuilder;
import com.postings.demo.post.builders.PostDtoBuilder;
import com.postings.demo.post.builders.TestJwtBuilder;
import com.postings.demo.post.dto.PostDto;
import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.model.Category;
import com.postings.demo.post.model.Post;
import com.postings.demo.post.repository.CategoryRepository;
import com.postings.demo.post.service.PostService;
import com.postings.demo.post.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
public class PostControllerTests {
	
	@Value("${service.base.uri}")
	private String baseUri ;
	
	@Autowired
	private MockMvc mockMvc ;
	
	@MockBean
	private PostService postService ;
	
	@MockBean
	private CategoryRepository categoryRepository ;
	
	private Category category ;
	
	@MockBean
	private UserService userService ;
	
	@Value("${jwt.secret}")
	private String secret ;
	
	private TestJwtBuilder jwtBuilder ;
	
	@BeforeEach
	public void setUp() {
		category = new Category(0L, "category", "description") ;
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category)) ;
		doNothing().when(userService).setStats(anyString(), anyString(), any());
		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("test@test.com", Set.of("USER"))) ;
		doNothing().when(userService).setStats(anyString(), anyString(), any());
		when(postService.findCountByUserId(anyString())).thenReturn(1) ;
		
		jwtBuilder = new TestJwtBuilder(secret) ;
	}
	
	@Test
	public void givenPostWhenIsSavedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.save(any())).thenReturn(post) ;
		ArgumentCaptor<Post> acPost = ArgumentCaptor.forClass(Post.class);
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/posts/" + post.getId()))
			.andExpect(jsonPath("$.id", is(post.getId().intValue())))
			.andExpect(jsonPath("$.title", is(post.getTitle())))
			.andExpect(jsonPath("$.body", is(post.getBody())))
			.andExpect(jsonPath("$.categoryId", is(category.getId().intValue())))
			.andExpect(jsonPath("$.public", is(post.isPublic())))
			.andExpect(jsonPath("$.stars", is(post.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andExpect(jsonPath("$.reference", is(post.getReference())))
			.andReturn();
		
		verify(postService).save(acPost.capture());
		assertThat(acPost.getValue().getUserId()).withFailMessage("post.userId must be set to userId header value").isEqualTo(USER_ID);
	}
	
	@Test
	public void givenPostWhenIsUpdatedThenSuccess() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		when(postService.update(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/posts/" + post.getId()))
			.andExpect(jsonPath("$.id", is(post.getId().intValue())))
			.andExpect(jsonPath("$.title", is(post.getTitle())))
			.andExpect(jsonPath("$.body", is(post.getBody())))
			.andExpect(jsonPath("$.categoryId", is(category.getId().intValue())))
			.andExpect(jsonPath("$.public", is(post.isPublic())))
			.andExpect(jsonPath("$.stars", is(post.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andExpect(jsonPath("$.reference", is(post.getReference())))
			.andReturn();
	}
	
	@Test
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		Post post = new PostBuilder().sample().build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/posts/" + post.getId()))
			.andExpect(jsonPath("$.id", is(post.getId().intValue())))
			.andExpect(jsonPath("$.title", is(post.getTitle())))
			.andExpect(jsonPath("$.body", is(post.getBody())))
			.andExpect(jsonPath("$.categoryId", is(category.getId().intValue())))
			.andExpect(jsonPath("$.public", is(post.isPublic())))
			.andExpect(jsonPath("$.stars", is(post.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andExpect(jsonPath("$.reference", is(post.getReference())))
			.andReturn();
	}
	
	@Test
	public void givenNoPageWhenIsGetThenSuccess() throws Exception {
		Page<Post> page = new PageImpl<>(new PostBuilder().samples(20)) ;
		
		when(postService.findByUserId(anyString(), any(Pageable.class))).thenReturn(page) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(20)))
			.andReturn();
	}
	
	@Test
	public void givenPageWhenIsGetThenSuccess() throws Exception {
		Page<Post> page = new PageImpl<>(new PostBuilder().samples(5)) ;
		
		when(postService.findByUserId(anyString(), any(Pageable.class))).thenReturn(page) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts")
				.param("page", "1")
				.param("size", "5")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(5)))
			.andReturn();
	}
	
	@Test
	public void givenSearchByOnlyTitleWhenIsGetThenSuccess() throws Exception {
		List<Post> list = new PostBuilder().samples(20) ;
		list.subList(0, 4).forEach(p -> p.setTitle("none"));
		
		when(postService.findByUserIdOrIsPublic(anyString(), anyBoolean())).thenReturn(list) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search")
				.param("text", "none")
				.param("onlyTitle", "true")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(4)))
			.andReturn();
	}
	
	@Test
	public void givenSearchByTitleAndBodyWhenIsGetThenSuccess() throws Exception {
		List<Post> list = new PostBuilder().samples(20) ;
		list.subList(0, 4).forEach(p -> p.setTitle("none"));
		list.subList(4, 8).forEach(p -> p.setBody(p.getBody() + "none"));
		
		when(postService.findByUserIdOrIsPublic(anyString(), anyBoolean())).thenReturn(list) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search")
				.param("text", "none")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(8)))
			.andReturn();
	}
	
	@Test
	public void givenSearchByHashtagsWhenIsGetThenSuccess() throws Exception {
		List<Post> list = new PostBuilder().samples(20) ;
		list.subList(0, 4).forEach(p -> p.getHashtags().add("t4"));
		list.subList(4, 8).forEach(p -> p.getHashtags().add("t5"));
		
		when(postService.findByUserIdOrIsPublic(anyString(), anyBoolean())).thenReturn(list) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search")
				.param("hashtags", "t4,t5")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(8)))
			.andReturn();
		
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void givenSearchByHashtagsAndPageWhenIsGetThenSuccess() throws Exception {
		List<Post> list = new PostBuilder().samples(20) ;
		list.subList(0, 4).forEach(p -> p.getHashtags().add("t4"));
		list.subList(4, 8).forEach(p -> p.getHashtags().add("t5"));
		
		when(postService.findByUserIdOrIsPublic(anyString(), anyBoolean())).thenReturn(list) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search")
				.param("hashtags", "t4,t5")
				.param("page", "1").param("size", "5")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(3)))
			.andReturn();
		
		System.out.println(result.getResponse().getContentAsString());
	}
	
	@Test
	public void givenSearchByHashtagsAndCategoryWhenIsGetThenSuccess() throws Exception {
		List<Post> list = new PostBuilder().samples(20) ;
		list.subList(0, 4).forEach(p -> p.getHashtags().add("t4"));
		list.subList(0, 2).forEach(p -> p.getCategory().setId(CATEGORY_ID + 1));
		list.subList(4, 8).forEach(p -> p.getHashtags().add("t5"));
		list.subList(4, 6).forEach(p -> p.getCategory().setId(CATEGORY_ID + 1));
		
		when(postService.findByUserIdOrIsPublic(anyString(), anyBoolean())).thenReturn(list) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/search")
				.param("hashtags", "t4,t5")
				.param("categoryId", String.valueOf(CATEGORY_ID))
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(4)))
			.andReturn();
	}
	
	@Test
	public void givenMissingUserIdWhenIsGetThenValidationError() throws Exception {
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/0"))
			.andExpect(status().isUnauthorized())
			.andReturn();
	}
	
	@Test
	public void givenMissingUserIdWhenIsDeleteThenValidationError() throws Exception {
		MvcResult result = mockMvc.perform(delete(baseUri + "/posts/0"))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenPostNotExistWhenIsGetThenNotFound() throws Exception {
		when(postService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void givenPostofOtherUserWhenIsGetThenForbidden() throws Exception {
		Post post = new PostBuilder().sample().userId(USER_ID + 1).build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenPublicPostofOtherUserWhenIsGetThenSuccess() throws Exception {
		Post post = new PostBuilder().sample().userId(USER_ID + 1).isPublic(true).build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/posts/" + post.getId()))
			.andExpect(jsonPath("$.id", is(post.getId().intValue())))
			.andExpect(jsonPath("$.title", is(post.getTitle())))
			.andExpect(jsonPath("$.body", is(post.getBody())))
			.andExpect(jsonPath("$.categoryId", is(category.getId().intValue())))
			.andExpect(jsonPath("$.public", is(post.isPublic())))
			.andExpect(jsonPath("$.stars", is(post.getStars())))
			.andExpect(jsonPath("$.hashtags", hasItems("t1", "t2", "t3")))
			.andExpect(jsonPath("$.reference", is(post.getReference())))
			.andReturn();
	}
	
	@Test
	public void givenPostWhenIsDeletedThenSuccess() throws Exception {
		Post post = new PostBuilder().sample().build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		doNothing().when(postService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andReturn();
	}
	
	@Test
	public void givenPostofOtherUserWhenIsDeletedThenForbidden() throws Exception {
		Post post = new PostBuilder().sample().userId(USER_ID + 1).build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		doNothing().when(postService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenMissingPostWhenIsDeletedThenNotFound() throws Exception {
		when(postService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void givenPostWhenServiceDeletedThrowsExceptionThenFailure() throws Exception {
		Post post = new PostBuilder().sample().build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		doThrow(RuntimeException.class).when(postService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenMissingPostWhenIsUpdatedThenNotFound() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.empty()) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isNotFound())
			.andReturn();
	}
	
	@Test
	public void givenPostofOtherUserWhenIsUpdatedThenForbidden() throws Exception {
		Post post = new PostBuilder().sample().userId(USER_ID + 1).build() ;
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenPostWhenServiceUpdateThrowsExceptionThenFailure() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		when(postService.update(any())).thenThrow(RuntimeException.class) ;		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenPostWhenServiceUpdateReturnsNullExceptionThenFailure() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		when(postService.update(any())).thenReturn(null);
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenPostWhenServiceSaveThrowsExceptionThenFailure() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		when(postService.save(any())).thenThrow(RuntimeException.class) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenPostWhenServiceSaveReturnsNullExceptionThenFailure() throws Exception {
		PostDto dto = new PostDtoBuilder().sample().build() ;
		
		when(postService.save(any())).thenReturn(null);
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isInternalServerError())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingTitleWhenIsSavedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingBodyWhenIsSavedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingCategoryWhenIsSavedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingPublicWhenIsSavedThenSuccessWithDefaultValueFalse() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").stars(3).category(0L).hashtags(List.of("t1")).reference("ref").build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.save(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.public", is(false)))
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingStarsWhenIsSavedThenSuccessWithDefaultValueZero() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).category(0L).hashtags(List.of("t1")).reference("ref").build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.save(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.stars", is(0)))
			.andReturn();
	}
	
	@Test
	public void givenPostWithInvalidStarsWhenIsSavedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").stars(10).isPublic(true).category(0L).hashtags(List.of("t1")).reference("ref").build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.save(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingUserIdWhenIsSavedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).stars(3).category(0L).hashtags(List.of("t1")).reference("ref").build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingHashtagsWhenIsSavedThenSuccessWithEmptyHashtags() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).stars(3).category(0L).reference("ref").build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.save(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isCreated())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingReferenceWhenIsSavedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).stars(3).category(0L).hashtags(List.of("t1")).build() ;
		
		MvcResult result = mockMvc.perform(post(baseUri + "/posts")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}

	@Test
	public void givenPostWithMissingTitleWhenIsUpdatedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().build() ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingBodyWhenIsUpdatedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").build() ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingCategoryWhenIsUpdatedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").build() ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingPublicWhenIsUpdatedThenSuccessWithDefaultValueFalse() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").stars(3).category(0L).hashtags(new ArrayList<String>(List.of("t1"))).reference("ref").build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		when(postService.update(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.public", is(false)))
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingStarsWhenIsUpdatedThenSuccessWithDefaultValueZero() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).category(0L).hashtags(new ArrayList<String>(List.of("t1"))).reference("ref").build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		when(postService.update(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.stars", is(0)))
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingUserIdWhenIsUpdatedThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).stars(3).category(0L).hashtags(List.of("t1")).reference("ref").build() ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingHashtagsWhenIsUpdatedThenSuccessWithEmptyHashtags() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).stars(3).category(0L).reference("ref").build() ;
		Post post = new PostBuilder().fromDto(dto).id(0L).build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		when(postService.update(any())).thenReturn(post) ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andReturn();
	}
	
	@Test
	public void givenPostWithMissingReferenceWhenIsUpdateddThenValidationError() throws Exception {
		PostDto dto = new PostDtoBuilder().title("title").body("body").isPublic(true).stars(3).category(0L).hashtags(List.of("t1")).build() ;
		
		MvcResult result = mockMvc.perform(put(baseUri + "/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isBadRequest())
			.andReturn();
	}
}