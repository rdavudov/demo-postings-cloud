package com.postings.demo.post;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.postings.demo.post.builders.PostBuilder;
import com.postings.demo.post.builders.TestJwtBuilder;
import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.model.Category;
import com.postings.demo.post.model.Post;
import com.postings.demo.post.model.User;
import com.postings.demo.post.repository.CategoryRepository;
import com.postings.demo.post.service.PostService;
import com.postings.demo.post.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
public class AdminPostControllerTests {
	
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
		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("test@test.com", Set.of("ADMIN"))) ;
		jwtBuilder = new TestJwtBuilder(secret) ;
	}
	
	@Test
	public void givenIdWhenIsGetThenSuccess() throws Exception {
		Post post = new PostBuilder().sample().build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/admin/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/admin/posts/" + post.getId()))
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
	public void givenNonAdminWhenIsGetThenForbidden() throws Exception {
		Post post = new PostBuilder().sample().build() ;
		
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("test@test.com", Set.of("USER"))) ;
		
		MvcResult result = mockMvc.perform(get(baseUri + "/admin/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isForbidden())
			.andReturn();
	}
	
	@Test
	public void givenPostWhenIsDeletedThenSuccess() throws Exception {
		Post post = new PostBuilder().sample().build() ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		doNothing().when(postService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/admin/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isOk())
			.andReturn();
	}
	
	@Test
	public void givenNonAdminWhenIsDeletedThenForbidden() throws Exception {
		Post post = new PostBuilder().sample().build() ;
		
		when(userService.getRoles(anyString(), anyString())).thenReturn(new UserRole("test@test.com", Set.of("USER"))) ;
		when(postService.findById(anyLong())).thenReturn(Optional.of(post)) ;
		doNothing().when(postService).delete(anyLong());
		
		MvcResult result = mockMvc.perform(delete(baseUri + "/admin/posts/0")
				.header("Authorization", "Bearer " + jwtBuilder.jwt()))
			.andExpect(status().isForbidden())
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
}