package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.DTO_LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.EMAIL;
import static com.postings.demo.user.builder.TestUserBuilder.FIRSTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.emptyUserBuilder;
import static com.postings.demo.user.builder.TestUserBuilder.fullUser;
import static com.postings.demo.user.builder.TestUserBuilder.fullUserBuilder;
import static com.postings.demo.user.builder.TestUserBuilder.testUser;
import static com.postings.demo.user.utility.JacksonUtility.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postings.demo.user.builder.TestJwtBuilder;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;
import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.repository.UserRoleRepository;
import com.postings.demo.user.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class AdminUserController {
	@Autowired
	private MockMvc mockMvc ;
	
	@Value("${service.base.uri}")
	private String baseUri ;
	
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
	public void givenUserWhenGetThenSuccess() throws Exception {
		User existingUser = fullUserBuilder().build() ;
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		
		mockMvc.perform(get(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/users/" + existingUser.getId()))
			.andExpect(jsonPath("$.id", is(existingUser.getId())))
			.andExpect(jsonPath("$.firstName", is(existingUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(existingUser.getLastName())))
			.andExpect(jsonPath("$.picture", is(existingUser.getPicture())))
			.andExpect(jsonPath("$.email", is(existingUser.getEmail())));
	}
	
	@Test
	public void givenNonAdminWhenGetThenForbidden() throws Exception {
		User existingUser = fullUserBuilder().build() ;
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		
		when(userRoleRepository.findById(anyString())).thenReturn(Optional.empty()) ;
		
		mockMvc.perform(get(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isForbidden()) ;
	}
	
	@Test
	public void givenUserWhenSaveThenSuccess() throws Exception {
		User user = fullUserBuilder().build() ;
		User savedUser = fullUserBuilder().build() ;
		
		doReturn(savedUser).when(userService).save(any()) ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(user)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/users/" + savedUser.getId()))
			.andExpect(jsonPath("$.id", org.hamcrest.CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.email", is(user.getEmail())))
			.andExpect(jsonPath("$.firstName", is(user.getFirstName())))
			.andExpect(jsonPath("$.picture", is(user.getPicture())))
			.andExpect(jsonPath("$.lastName", is(user.getLastName())));
	}
	
	@Test
	public void givenUserWhenExceptionDuringSaveThenFailure() throws Exception {
		User user = fullUserBuilder().build() ;
		
		doThrow(RuntimeException.class).when(userService).save(any()) ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(user)))
			.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void givenInvalidEmailWhenSavedThenValidationError() throws Exception {
		User user = fullUserBuilder().email("invalid").build() ;
		
		doThrow(RuntimeException.class).when(userService).save(any()) ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(user)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void givenExistingEmailWhenSaveThenValidationError() throws Exception {
		doReturn(Optional.of(testUser())).when(userService).findByEmail(EMAIL) ;
		
		User user = fullUserBuilder().build() ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(user)))
			.andExpect(status().isBadRequest()) ;
	}
	
	@Test
	public void givenUsersWhenGetThenSuccess() throws Exception {
		doReturn(List.of(fullUser(), fullUserBuilder().id(ID + "2").build())).when(userService).findAll() ;
		
		MvcResult result = mockMvc.perform(get(getBaseUrl()).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(2)))
			.andExpect(jsonPath("$[0].id", is(ID)))
			.andExpect(jsonPath("$[0].email", is(EMAIL)))
			.andExpect(jsonPath("$[0].firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$[0].lastName", is(LASTNAME)))			
			.andExpect(jsonPath("$[1].id", is(ID + "2")))
			.andExpect(jsonPath("$[1].email", is(EMAIL)))
			.andExpect(jsonPath("$[1].firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$[1].lastName", is(LASTNAME)))			
			.andReturn();
	}
	
	@Test
	public void givenEmailWhenGetThenSuccess() throws Exception {
		doReturn(List.of(fullUser(), fullUserBuilder().id(ID + "2").build())).when(userService).findAll() ;
		
		MvcResult result = mockMvc.perform(get(getBaseUrl()).requestAttr("email", EMAIL).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(2)))
			.andExpect(jsonPath("$[0].id", is(ID)))
			.andExpect(jsonPath("$[0].email", is(EMAIL)))
			.andExpect(jsonPath("$[0].firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$[0].lastName", is(LASTNAME)))			
			.andExpect(jsonPath("$[1].id", is(ID + "2")))
			.andExpect(jsonPath("$[1].email", is(EMAIL)))
			.andExpect(jsonPath("$[1].firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$[1].lastName", is(LASTNAME)))			
			.andReturn();
	}
	
	@Test
	public void givenUserWhenUpdatedThenSuccess() throws Exception {
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setLastName(DTO_LASTNAME);
		User existingUser = fullUserBuilder().build() ;
		User updatedUser = fullUserBuilder().lastname(DTO_LASTNAME).build() ;
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		doReturn(Optional.of(updatedUser)).when(userService).update(anyString(), any(UserUpdateDto.class)) ;
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/users/" + existingUser.getId()))
			.andExpect(jsonPath("$.id", org.hamcrest.CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.email", is(updatedUser.getEmail())))
			.andExpect(jsonPath("$.firstName", is(updatedUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(updatedUser.getLastName())));
	}
	
	@Test
	public void givenUserWhenDeletedThenSuccess() throws Exception {
		doReturn(Optional.of(fullUser())).when(userService).findById(ID) ;
		
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isNoContent()) ;
	}
	
	@Test
	public void givenMissinUserWhenDeletedThenNotFound() throws Exception {
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isNotFound()) ;
	}
	
	@Test
	public void givenUserWhenExceptionDuringDeletionThenFailure() throws Exception {
		doReturn(Optional.of(fullUser())).when(userService).findById(ID) ;
		doThrow(RuntimeException.class).when(userService).delete(ID) ;
		
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isInternalServerError()) ;
	}
	
	public static String getBaseUrl() {
		return "/api/v1/admin/users" ;
	}
}
