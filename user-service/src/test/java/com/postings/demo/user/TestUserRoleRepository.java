package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.EMAIL;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.postings.demo.user.extension.MongoDataFile;
import com.postings.demo.user.extension.MongoExtension;
import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.repository.UserRoleRepository;

@DataMongoTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(MongoExtension.class)
public class TestUserRoleRepository {
	
	@Autowired
	private UserRoleRepository repository ;
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	@Test
	@MongoDataFile(value = "user_roles3.json", classType = UserRole.class, collectionName = "UserRoles")
	public void givenUserRolesWhenFindAllThenSuccess() {
		List<UserRole> roles = repository.findAll() ;
		assertThat(roles).hasSize(3) ;
	}
	
	@Test
	@MongoDataFile(value = "user_roles3.json", classType = UserRole.class, collectionName = "UserRoles")
	public void givenUserRolesWhenFindByEmailThenSuccess() {
		Optional<UserRole> role = repository.findById(EMAIL) ;
		assertThat(role).isPresent() ;
	}
	
	@Test
	@MongoDataFile(value = "user_roles0.json", classType = UserRole.class, collectionName = "UserRoles")
	public void givenMissingUserRolesWhenFindByEmailThenFailure() {
		Optional<UserRole> role = repository.findById(EMAIL) ;
		assertThat(role).isEmpty() ;
	}
	
	@Test
	@MongoDataFile(value = "user_roles0.json", classType = UserRole.class, collectionName = "UserRoles")
	public void givenUserRoleWhenIsSavedThenSuccess() {
		UserRole role = new UserRole(EMAIL, Set.of("ROLE1", "ROLE2")) ;
		repository.save(role);
		
		Optional<UserRole> saved = repository.findById(EMAIL) ;
		assertThat(saved).isPresent() ;
		UserRole savedRole = saved.get() ;
		assertThat(savedRole.getEmail()).isEqualTo(EMAIL) ;
	}
	
	@Test
	@MongoDataFile(value = "user_roles3.json", classType = UserRole.class, collectionName = "UserRoles")
	public void givenUserRoleWhenDeleteThenSuccess() {
		repository.deleteById(EMAIL) ;
		
		Optional<UserRole> role = repository.findById(EMAIL) ;
		assertThat(role).isEmpty() ;
	}
}
