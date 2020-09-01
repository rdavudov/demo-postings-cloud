package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.DTO_LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.OTHER_ID;
import static com.postings.demo.user.builder.TestUserBuilder.testUserBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.postings.demo.user.utility.JacksonUtility.toJson;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postings.demo.user.builder.TestJwtBuilder;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;
import com.postings.demo.user.model.UserStats;
import com.postings.demo.user.service.UserService;
import com.postings.demo.user.service.UserStatsService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class TestUserController {
	@Autowired
	private MockMvc mockMvc ;
	
	@MockBean
	private UserService userService ;
	
	@MockBean
	private UserStatsService userStatsService ;
	
	@Value("${service.base.uri}")
	private String baseUri ;

	@Value("${jwt.secret}")
	private String secret ;
	
	private TestJwtBuilder jwtBuilder ;
	
	@BeforeEach
	public void setUp() {
		jwtBuilder = new TestJwtBuilder(secret) ;
	}
	
	@Test
	public void givenUserWhenGetThenSuccess() throws Exception {
		User existingUser = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		doReturn(Optional.empty()).when(userStatsService).findById(ID) ;

		mockMvc.perform(get(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtBuilder.jwt(existingUser)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + existingUser.getId()))
			.andExpect(jsonPath("$.id", is(existingUser.getId())))
			.andExpect(jsonPath("$.firstName", is(existingUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(existingUser.getLastName())))
			.andExpect(jsonPath("$.picture", is(existingUser.getPicture())))
			.andExpect(jsonPath("$.stats").exists())
			.andExpect(jsonPath("$.stats.posts", is(0)))
			.andExpect(jsonPath("$.stats.following", is(0)))
			.andExpect(jsonPath("$.stats.followers", is(0)))
			.andExpect(jsonPath("$.email", is(existingUser.getEmail())));
	}
	
	@Test
	public void givenMissingUserWhenGetThenCreateUser() throws Exception {
		User newUser = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		doReturn(newUser).when(userService).save(any()) ;

		mockMvc.perform(get(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtBuilder.jwt(newUser)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + newUser.getId()))
			.andExpect(jsonPath("$.id", is(newUser.getId())))
			.andExpect(jsonPath("$.firstName", is(newUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(newUser.getLastName())))
			.andExpect(jsonPath("$.picture", is(newUser.getPicture())))
			.andExpect(jsonPath("$.email", is(newUser.getEmail())));
		
		verify(userService).save(any()) ;
	}
	
	@Test
	public void givenUserWithStatsWhenGetThenSuccess() throws Exception {
		User existingUser = testUserBuilder().id(ID).build() ;
		UserStats userStats = new UserStats() ;
		userStats.setId(ID);
		userStats.setPosts(5);
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		doReturn(Optional.of(userStats)).when(userStatsService).findById(ID) ;

		mockMvc.perform(get(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtBuilder.jwt(existingUser)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + existingUser.getId()))
			.andExpect(jsonPath("$.id", is(existingUser.getId())))
			.andExpect(jsonPath("$.firstName", is(existingUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(existingUser.getLastName())))
			.andExpect(jsonPath("$.picture", is(existingUser.getPicture())))
			.andExpect(jsonPath("$.stats").exists())
			.andExpect(jsonPath("$.stats.posts", is(5)))
			.andExpect(jsonPath("$.stats.following", is(0)))
			.andExpect(jsonPath("$.stats.followers", is(0)))
			.andExpect(jsonPath("$.email", is(existingUser.getEmail())));
	}
	
	@Test
	public void givenUserWhenGetOtherUserThenSuccess() throws Exception {
		User existingUser = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;

		mockMvc.perform(get(getBaseUrl() + "/{id}", OTHER_ID).header("Authorization", "Bearer " + jwtBuilder.jwt(existingUser)))
			.andExpect(status().isForbidden()) ;
	}
	
	@Test
	public void givenUserWhenOtherIdThenUnauthorized() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		mockMvc.perform(get(getBaseUrl() + "/{id}", OTHER_ID).header("Authorization", "Bearer " + jwtBuilder.jwt(user)))
			.andExpect(status().isForbidden()) ;
	}
	
	@Test
	public void givenUserWhenSavedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		User savedUser = testUserBuilder().id(ID).build() ;
		
		doReturn(savedUser).when(userService).save(any()) ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtBuilder.jwt(user)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + savedUser.getId()))
			.andExpect(jsonPath("$.id", org.hamcrest.CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.email", is(user.getEmail())))
			.andExpect(jsonPath("$.firstName", is(user.getFirstName())))
			.andExpect(jsonPath("$.picture", is(user.getPicture())))
			.andExpect(jsonPath("$.lastName", is(user.getLastName())));
	}
	
	@Test
	public void givenUserWhenExceptionInSaveThenFailure() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doThrow(RuntimeException.class).when(userService).save(any()) ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtBuilder.jwt(user)))
			.andExpect(status().isInternalServerError());
	}

		
	@Test
	public void givenExistingUserWhenSavedThenFailure() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		User existingUser = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtBuilder.jwt(user)))
			.andExpect(status().isBadRequest()) ;
	}
	
	@Test
	public void givenUserNotVerifiedEmailWhenSavedThenFailure() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		jwtBuilder.emailVerified(false) ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtBuilder.jwt(user)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void givenUserWhenUpdatedThenSuccess() throws Exception {
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setLastName(DTO_LASTNAME);
		User existingUser = testUserBuilder().id(ID).build() ;
		User updatedUser = testUserBuilder().id(ID).lastname(DTO_LASTNAME).build() ;
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		doReturn(Optional.of(updatedUser)).when(userService).update(anyString(), any(UserUpdateDto.class)) ;
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtBuilder.jwt(existingUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + existingUser.getId()))
			.andExpect(jsonPath("$.id", org.hamcrest.CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.email", is(updatedUser.getEmail())))
			.andExpect(jsonPath("$.firstName", is(updatedUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(updatedUser.getLastName())))
			.andExpect(jsonPath("$.picture", is(updatedUser.getPicture())));
	}
	
	@Test
	public void givenUserWhenUpdatedOtherUserThenSuccess() throws Exception {
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setLastName(DTO_LASTNAME);
		User existingUser = testUserBuilder().id(ID).build() ;
		User updatedUser = testUserBuilder().id(ID).lastname(DTO_LASTNAME).build() ;
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		doReturn(Optional.of(updatedUser)).when(userService).update(ID, dto) ;
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", OTHER_ID).header("Authorization", "Bearer " + jwtBuilder.jwt(updatedUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isForbidden()) ;
	}
	
	public static String objectAsJsonString(Object object) throws Exception {
		return new ObjectMapper().writeValueAsString(object) ;
	}
	
	public String getBaseUrl() {
		return baseUri + "/users" ;
	}
}
