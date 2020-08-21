package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.postings.demo.user.extension.MongoDataFile;
import com.postings.demo.user.extension.MongoExtension;
import com.postings.demo.user.model.Blocked;
import com.postings.demo.user.repository.BlockedRepository;

@DataMongoTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(MongoExtension.class)
public class TestBlockedRepository {
	
	@Autowired
	private BlockedRepository repository ;
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	@Test
	@DisplayName("test find all repository Success")
	@MongoDataFile(value = "blocked2.json", classType = Blocked.class, collectionName = "Blocked")
	public void givenBlockedUsersWhenFindAllThenSuccess() {
		List<Blocked> blocked = repository.findAll() ;
		assertThat(blocked).hasSize(2) ;
	}
	
	@Test
	@DisplayName("test find by id repository Success")
	@MongoDataFile(value = "blocked2.json", classType = Blocked.class, collectionName = "Blocked")
	public void givenBlockedUsersWhenFindByIdThenSuccess() {
		Optional<Blocked> blocked = repository.findById(ID) ;
		assertThat(blocked).isPresent() ;
	}
	
	@Test
	@DisplayName("test find by id repository NotFound")
	@MongoDataFile(value = "blocked0.json", classType = Blocked.class, collectionName = "Blocked")
	public void givenMissingBlockedUsersWhenFindByIdThenFailure() {
		Optional<Blocked> blocked = repository.findById(ID) ;
		assertThat(blocked).withFailMessage("blocked must not be found").isEmpty() ;
	}
	
	@Test
	@DisplayName("test save blocked repository Success")
	@MongoDataFile(value = "blocked0.json", classType = Blocked.class, collectionName = "Blocked")
	public void givenBlockedWhenIsSavedThenSuccess() {
		Blocked blocked = new Blocked() ;
		blocked.setId(ID);
		repository.save(blocked);
		
		Optional<Blocked> saved = repository.findById(ID) ;
		assertThat(saved).isPresent() ;
		Blocked savedBlocked = saved.get() ;
		assertThat(savedBlocked.getId()).isEqualTo(ID) ;
	}
	
	@Test
	@DisplayName("test save blocked repository Success")
	@MongoDataFile(value = "blocked2.json", classType = Blocked.class, collectionName = "Blocked")
	public void testSaveUserCheckFindAllSuccess() {
		Blocked blocked = new Blocked() ;
		blocked.setId(ID+"x");
		repository.save(blocked);
		
		List<Blocked> blockedList = repository.findAll() ;
		assertThat(blockedList).hasSize(3) ;
	}
	
	@Test
	@DisplayName("test find by id repository NotFound")
	@MongoDataFile(value = "blocked2.json", classType = Blocked.class, collectionName = "Blocked")
	public void testDeleteSuccess() {
		repository.deleteById(ID) ;
		
		Optional<Blocked> blocked = repository.findById(ID) ;
		assertThat(blocked).isEmpty() ;
	}
}
