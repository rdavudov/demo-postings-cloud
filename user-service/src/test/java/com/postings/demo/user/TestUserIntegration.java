package com.postings.demo.user;

import static com.postings.demo.user.TestUserController.getBaseUrl;
import static com.postings.demo.user.builder.TestUserBuilder.DTO_LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.EMAIL;
import static com.postings.demo.user.builder.TestUserBuilder.FIRSTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.testUserBuilder;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.extension.MongoDataFile;
import com.postings.demo.user.extension.MongoExtension;
import com.postings.demo.user.model.User;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith({SpringExtension.class, MongoExtension.class})
@AutoConfigureMockMvc
public class TestUserIntegration {
	@Autowired
	private MockMvc mockMvc ;
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	@Test
	@DisplayName("GET /users/{id} Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testGetUserSuccess() throws Exception {
		mockMvc.perform(get(getBaseUrl() + "/{id}", ID))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" +ID))
			.andExpect(jsonPath("$.id", is(ID)))
			.andExpect(jsonPath("$.email", is(EMAIL)))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(LASTNAME)));
	}
	
	@Test
	@DisplayName("POST /users Success")
	@MongoDataFile(value = "users0.json", classType = User.class, collectionName = "Users")
	public void testCreateUserSuccess() throws Exception {
		User user = testUserBuilder().build() ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(user.toString()))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", any(String.class)))
			.andExpect(jsonPath("$.id", not(ID)))
			.andExpect(jsonPath("$.email", is(user.getEmail())))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(LASTNAME)));
	}
	
	@Test
	@DisplayName("POST /users Success")
	@MongoDataFile(value = "users0.json", classType = User.class, collectionName = "Users")
	public void testCreateFullUserSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(user.toString()))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, any(String.class)))
			.andExpect(jsonPath("$.id", any(String.class)))
			.andExpect(jsonPath("$.id", not(ID)))
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
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(dto.toString()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + ID))
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
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(user.toString()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + ID))
			.andExpect(jsonPath("$.id", not(ID + "123")))
			.andExpect(jsonPath("$.email", not(EMAIL + "123")))
			.andExpect(jsonPath("$.firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$.lastName", is(DTO_LASTNAME)));
	}
	
	@Test
	@DisplayName("DELETE /users/{id} Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testDeleteUserSuccess() throws Exception {
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID))
			.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("GET /users Success")
	@MongoDataFile(value = "users4.json", classType = User.class, collectionName = "Users")
	public void testGetAllUsersSuccess() throws Exception {
		mockMvc.perform(get(getBaseUrl()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(4)));
	}
}
