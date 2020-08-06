package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.EMAIL;
import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.PASSWORD;
import static com.postings.demo.user.builder.TestUserBuilder.USERNAME;
import static com.postings.demo.user.builder.TestUserBuilder.VERSION;
import static com.postings.demo.user.builder.TestUserBuilder.fullUser;
import static com.postings.demo.user.builder.TestUserBuilder.fullUserBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.postings.demo.user.extension.MongoDataFile;
import com.postings.demo.user.extension.MongoExtension;
import com.postings.demo.user.model.User;
import com.postings.demo.user.repository.UserRepositoy;

@DataMongoTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(MongoExtension.class)
public class TestUserRepository {
	
	@Autowired
	private UserRepositoy repository ;
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	@Test
	@DisplayName("test find all repository Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testFindAll() {
		List<User> users = repository.findAll() ;
		assertThat(users).withFailMessage("list size must be 2").hasSize(2) ;
	}
	
	@Test
	@DisplayName("test find by id repository Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testFindByIdSuccess() {
		Optional<User> user = repository.findById(ID) ;
		assertThat(user).withFailMessage("user must be found").isPresent() ;
	}
	
	@Test
	@DisplayName("test find by id repository NotFound")
	@MongoDataFile(value = "users0.json", classType = User.class, collectionName = "Users")
	public void testFindByIdNotFound() {
		Optional<User> user = repository.findById(ID) ;
		assertThat(user).withFailMessage("user must not be found").isEmpty() ;
	}
	
	@Test
	@DisplayName("test find by id repository Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testFindByUsernameSuccess() {
		Optional<User> user = repository.findByUsername(USERNAME) ;
		assertThat(user).withFailMessage("user must be found").isPresent() ;
	}
	
	@Test
	@DisplayName("test find by username repository Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testFindByEmailSuccess() {
		Optional<User> user = repository.findByEmail(EMAIL) ;
		assertThat(user).withFailMessage("user must be found").isPresent() ;
	}
	
	@Test
	@DisplayName("test save user repository Success")
	@MongoDataFile(value = "users0.json", classType = User.class, collectionName = "Users")
	public void testSaveUserSuccess() {
		repository.save(fullUser());
		
		Optional<User> user = repository.findById(ID) ;
		assertThat(user).withFailMessage("user must be found").isPresent() ;
		User savedUser = user.get() ;
		assertThat(savedUser.getId()).isEqualTo(ID) ;
		assertThat(savedUser.getUsername()).isEqualTo(USERNAME) ;
		assertThat(savedUser.getPassword()).isEqualTo(PASSWORD) ;
		assertThat(savedUser.getEmail()).isEqualTo(EMAIL) ;
		assertThat(savedUser.getVersion()).isEqualTo(VERSION) ;
	}
	
	@Test
	@DisplayName("test save user repository Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testSaveUserCheckFindAllSuccess() {
		repository.save(fullUserBuilder().id(ID + "3").build());
		
		List<User> users = repository.findAll() ;
		assertThat(users).withFailMessage("list size must be 3").hasSize(3) ;
	}
	
	@Test
	@DisplayName("test save user repository Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testUpdateUserSuccess() {
		String modifiedEmail = EMAIL + ".test" ;
		repository.save(fullUserBuilder().email(modifiedEmail).build());
		
		Optional<User> user = repository.findById(ID) ;
		assertThat(user).withFailMessage("user must be found").isPresent() ;
		User savedUser = user.get() ;
		assertThat(savedUser.getId()).isEqualTo(ID) ;
		assertThat(savedUser.getUsername()).isEqualTo(USERNAME) ;
		assertThat(savedUser.getPassword()).isEqualTo(PASSWORD) ;
		assertThat(savedUser.getEmail()).isEqualTo(modifiedEmail) ;
		assertThat(savedUser.getVersion()).isEqualTo(VERSION) ;
	}
	
	@Test
	@DisplayName("test find by id repository NotFound")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testDeleteSuccess() {
		repository.deleteById(ID) ;
		
		Optional<User> user = repository.findById(ID) ;
		assertThat(user).withFailMessage("user must not be found").isEmpty() ;
	}
	
	@Test
	@DisplayName("test find all blocked repository Success")
	@MongoDataFile(value = "users2.json", classType = User.class, collectionName = "Users")
	public void testFindAllBlocked() {
		User userBlocked = new User() ;
		userBlocked.setIsBlocked(true);
		
		List<User> users = repository.findAll(Example.of(userBlocked)) ;
		assertThat(users).withFailMessage("list size must be 1").hasSize(1) ;
	}
}
