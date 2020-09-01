package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.*;
import static com.postings.demo.user.builder.TestUserBuilder.emptyUserBuilder;
import static com.postings.demo.user.builder.TestUserBuilder.testUserBuilder;
import static com.postings.demo.user.utility.JacksonUtility.toJson;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.postings.demo.user.builder.TestJwtBuilder;
import com.postings.demo.user.dto.UserStatsDto;
import com.postings.demo.user.model.User;
import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.model.UserStats;
import com.postings.demo.user.service.UserService;
import com.postings.demo.user.service.UserStatsService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class TestUserStatsController {
	
	@Value("${service.base.uri:/api/v1}/stats")
	private String baseUri ;
	
	@Value("${system.password}")
	private String systemPassword ;

	@Autowired
	private MockMvc mockMvc ; 
	
	@MockBean
	private UserService userService ;
	
	@MockBean
	private UserStatsService userStatsService ;
	
	private TestJwtBuilder jwtBuilder ;
	
	@Value("${jwt.secret}")
	private String secret ;
	
	private String jwtToken ;
	
	@BeforeEach
	public void setUp() {
		jwtBuilder = new TestJwtBuilder(secret) ;
		User admin = emptyUserBuilder().id(ID).email(EMAIL).firstname(FIRSTNAME).lastname(LASTNAME).picture(PICTURE).build() ;
		jwtToken = jwtBuilder.jwt(admin) ;
	}
	
	@Test
	public void givenUserStatsWhenSavedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.empty()).when(userStatsService).findById(ID) ;
		UserStatsDto stats = new UserStatsDto(ID, 5, null, null) ;

		mockMvc.perform(post(baseUri + "/{id}", ID)
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(stats)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void givenExistingUserStatsWhenSavedThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		UserStats existing = new UserStats(ID, 0, 5, 5) ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.of(existing)).when(userStatsService).findById(ID) ;
		UserStatsDto stats = new UserStatsDto(ID, 5, null, null) ;

		mockMvc.perform(post(baseUri + "/{id}", ID)
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(stats)))
		.andExpect(status().isOk());
	}
	
	@Test
	public void givenMissingUserStatsWhenSavedThenSuccess() throws Exception {
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		UserStatsDto stats = new UserStatsDto(ID, 5, null, null) ;

		mockMvc.perform(post(baseUri + "/{id}", ID)
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(stats)))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void givenOtherUserStatsWhenSavedThenForbidden() throws Exception {
		UserStatsDto stats = new UserStatsDto(ID, 5, null, null) ;

		mockMvc.perform(post(baseUri + "/{id}", ID + "2")
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(stats)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	public void givenUserStatsWhenExceptionDuringSaveThenFailure() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(Optional.empty()).when(userStatsService).findById(ID) ;
		doThrow(RuntimeException.class).when(userStatsService).save(any()) ;
		UserStatsDto stats = new UserStatsDto(ID, 5, null, null) ;

		mockMvc.perform(post(baseUri + "/{id}", ID)
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(stats)))
		.andExpect(status().isInternalServerError());
	}
}
