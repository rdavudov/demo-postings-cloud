package com.postings.demo.user;

import static com.postings.demo.user.AdminUserController.getBaseUrl;
import static com.postings.demo.user.builder.TestUserBuilder.DTO_LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.EMAIL;
import static com.postings.demo.user.builder.TestUserBuilder.FIRSTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.emptyUserBuilder;
import static com.postings.demo.user.builder.TestUserBuilder.testUserBuilder;
import static com.postings.demo.user.utility.JacksonUtility.toJson;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.postings.demo.user.builder.TestJwtBuilder;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.extension.MongoDataFile;
import com.postings.demo.user.extension.MongoExtension;
import com.postings.demo.user.model.User;
import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.repository.UserRoleRepository;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith({SpringExtension.class, MongoExtension.class})
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
public class AdminUserIntegration {
	@Autowired
	private MockMvc mockMvc ;
	
	@Value("${service.base.uri}")
	private String baseUri ;
	
	@MockBean
	private UserRoleRepository userRoleRepository ;
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
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
	@DisplayName("GET /users/{id} Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testGetUserSuccess() throws Exception {
		mockMvc.perform(get(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/users/" +ID))
			.andExpect(jsonPath("$.id", is(ID)))
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(LASTNAME)));
	}
	
	@Test
	@DisplayName("POST /users Success")
	@MongoDataFile(value = "users0.json", classType = User.class, collectionName = "Users")
	public void testCreateUserSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(user)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", is(ID)))
			.andExpect(jsonPath("$.email", is(user.getEmail())))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(LASTNAME)));
	}
	
	@Test
	@DisplayName("POST /users Success")
	@MongoDataFile(value = "users0.json", classType = User.class, collectionName = "Users")
	public void testCreateFullUserSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		mockMvc.perform(post(getBaseUrl()).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(user)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", is(ID)))
			.andExpect(jsonPath("$.email", is(user.getEmail())))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(LASTNAME)));
	}
	
	@Test
	@DisplayName("PUT /users Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testUpdateUserSuccess() throws Exception {
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setLastName(DTO_LASTNAME);
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(dto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/users/" + ID))
			.andExpect(jsonPath("$.id", any(String.class)))
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(DTO_LASTNAME)));
	}
	
	@Test
	@DisplayName("PUT /users Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testUpdateUserFailUpdateUsername() throws Exception {
		User user = testUserBuilder().build() ;
		user.setLastName(DTO_LASTNAME);
		user.setId(ID + "123");
		user.setEmail(EMAIL + "123");
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(user)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, baseUri + "/users/" + ID))
			.andExpect(jsonPath("$.id", not(ID + "123")))
			.andExpect(jsonPath("$.email", not(EMAIL + "123")))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(DTO_LASTNAME)));
	}
	
	@Test
	@DisplayName("DELETE /users/{id} Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testDeleteUserSuccess() throws Exception {
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("GET /users Success")
	@MongoDataFile(value = "users4.json", classType = User.class, collectionName = "Users")
	public void testGetAllUsersSuccess() throws Exception {
		mockMvc.perform(get(getBaseUrl()).header("Authorization", "Bearer " + jwtToken))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(4)));
	}
}
