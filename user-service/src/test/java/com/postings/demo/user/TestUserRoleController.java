package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.ADMIN_ID;
import static com.postings.demo.user.builder.TestUserBuilder.EMAIL;
import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.ROLES;
import static com.postings.demo.user.builder.TestUserBuilder.emptyUserBuilder;
import static com.postings.demo.user.builder.TestUserBuilder.testUserBuilder;
import static com.postings.demo.user.utility.JacksonUtility.toJson;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.postings.demo.user.builder.TestJwtBuilder;
import com.postings.demo.user.model.User;
import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.repository.UserRoleRepository;
import com.postings.demo.user.service.UserRoleService;
import com.postings.demo.user.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class TestUserRoleController {
	
	@Value("${service.base.uri:/api/v1}/roles")
	private String baseUri ;

	@Autowired
	private MockMvc mockMvc ; 
	
	@MockBean
	private UserRoleService userRoleService ;
	
	@MockBean
	private UserRoleRepository userRoleRepository ;
	
	@MockBean
	private UserService userService ;
	
	private TestJwtBuilder jwtBuilder ;
	
	@Value("${jwt.secret}")
	private String secret ;
	
	private String jwtToken ;
	
	@BeforeEach
	public void setUp() {
		jwtBuilder = new TestJwtBuilder(secret) ;
		User admin = emptyUserBuilder().id("adminid").email("admin@admin.com").firstname("admin").lastname("admin").picture("http://adminpic.com").build() ;
		jwtToken = jwtBuilder.jwt(admin) ;
		when(userRoleRepository.findById(anyString())).thenReturn(Optional.of(new UserRole("admin@admin.com", Set.of("ADMIN")))) ;
	}
	
	@Test
	public void givenUserRoleWhenSavedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doNothing().when(userRoleService).save(any(UserRole.class));
		
		mockMvc.perform(post(baseUri + "/{id}/add", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isOk());
		
		verify(userRoleService).get(EMAIL) ;
	}
	
	@Test
	public void givenUserRoleWithExistingRolesWhenSavedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, Set.of("ROLE5", "ROLE6")) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doNothing().when(userRoleService).save(any(UserRole.class));
		
		UserRole existing = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.of(existing)).when(userRoleService).get(anyString());
		
		mockMvc.perform(post(baseUri + "/{id}/add", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is(EMAIL)))
				.andExpect(jsonPath("$.roles", hasItems("ROLE1", "ROLE2", "ROLE5", "ROLE6")))
				.andExpect(jsonPath("$.roles.length()", is(4)));
		
		verify(userRoleService).get(EMAIL) ;
	}
	
	@Test
	public void givenUserRoleWhenExceptionDuringSaveThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doThrow(RuntimeException.class).when(userRoleService).save(any(UserRole.class));
		
		mockMvc.perform(post(baseUri + "/{id}/add", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void givenMissingUserWhenSaveThenNotFound() throws Exception {
		UserRole role = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		
		mockMvc.perform(post(baseUri + "/{id}/add", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void givenOwnUserRoleWhenSavedThenInvalid() throws Exception {
		UserRole role = new UserRole(EMAIL, ROLES) ;
		
		mockMvc.perform(post(baseUri + "/{id}/add", ADMIN_ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void givenUserRoleWhenGetThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.of(role)).when(userRoleService).get(anyString());
		
		mockMvc.perform(get(baseUri + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.roles", hasItems("ROLE1", "ROLE2")))
			.andExpect(jsonPath("$.roles.length()", is(2)));
	}
	
	@Test
	public void givenUserRoleWhenDeletedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, Set.of("ROLE1")) ;
		UserRole existing = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.of(existing)).when(userRoleService).get(anyString());
		
		mockMvc.perform(post(baseUri + "/{id}/remove", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.roles", hasItems("ROLE2")))
			.andExpect(jsonPath("$.roles.length()", is(1)));
		
		verify(userRoleService).save(any());
	}
	
	@Test
	public void givenUserRolesWhenAllRolesDeletedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, Set.of("ROLE1", "ROLE2")) ;
		UserRole existing = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.of(existing)).when(userRoleService).get(anyString());
		
		mockMvc.perform(post(baseUri + "/{id}/remove", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.roles.length()", is(0)));
		
		verify(userRoleService).delete(EMAIL);
	}
	
	@Test
	public void givenNotExistingUserRolesWhenAllRolesDeletedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, Set.of("ROLE1", "ROLE2", "ROLE3")) ;
		UserRole existing = new UserRole(EMAIL, ROLES) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.of(existing)).when(userRoleService).get(anyString());
		
		mockMvc.perform(post(baseUri + "/{id}/remove", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.roles.length()", is(0)));
		
		verify(userRoleService).delete(EMAIL);
	}
	
	@Test
	public void givenOwnUserRolesWhenDeletedThenFailure() throws Exception {
		UserRole role = new UserRole(EMAIL, Set.of("ROLE1", "ROLE2", "ROLE3")) ;
		
		mockMvc.perform(post(baseUri + "/{id}/remove", ADMIN_ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void givenEmptyUserRolesWhenDeletedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, Set.of("ROLE1", "ROLE2", "ROLE3")) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.empty()).when(userRoleService).get(anyString());
		
		mockMvc.perform(post(baseUri + "/{id}/remove", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.roles.length()", is(0)));
	}
	
	@Test
	public void givenMissingUserRoleWhenGetThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.empty()).when(userRoleService).get(anyString());
		
		mockMvc.perform(get(baseUri + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.roles.length()", is(0)));
	}
	
	@Test
	public void givenMissingUserWhenGetThenSuccess() throws Exception {
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		
		mockMvc.perform(get(baseUri + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void givenUserRoleWhenExceptionDuringGetThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doThrow(RuntimeException.class).when(userRoleService).get(anyString());
		
		mockMvc.perform(get(baseUri + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void givenUserRoleWhenExceptionDuringDeleteThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserRole role = new UserRole(EMAIL, Set.of("ROLE1", "ROLE2", "ROLE3")) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doThrow(RuntimeException.class).when(userRoleService).get(anyString());
		
		mockMvc.perform(post(baseUri + "/{id}/remove", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(role)))
			.andExpect(status().isInternalServerError());
	}
}
