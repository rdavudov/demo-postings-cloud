package com.postings.demo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static com.postings.demo.user.builder.TestUserBuilder.*;

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
import com.postings.demo.user.model.UserStats;
import com.postings.demo.user.repository.UserStatsRepository;

@DataMongoTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(MongoExtension.class)
public class TestUserStatsRepository {
	
	@Autowired
	private UserStatsRepository repository ;
	
	@Autowired
	private MongoTemplate mongoTemplate ;
	
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
	
	@Test
	@DisplayName("test find all repository Success")
	@MongoDataFile(value = "user_stats2.json", classType = UserStats.class, collectionName = "UserStats")
	public void testFindAll() {
		List<UserStats> users = repository.findAll() ;
		assertThat(users).hasSize(2) ;
	}
	
	@Test
	@DisplayName("test find by id repository Success")
	@MongoDataFile(value = "user_stats2.json", classType = UserStats.class, collectionName = "UserStats")
	public void testFindByIdSuccess() {
		Optional<UserStats> user = repository.findById(ID) ;
		assertThat(user).isPresent() ;
	}
	
	@Test
	@DisplayName("test find by id repository NotFound")
	@MongoDataFile(value = "user_stats0.json", classType = UserStats.class, collectionName = "UserStats")
	public void testFindByIdNotFound() {
		Optional<UserStats> user = repository.findById(ID) ;
		assertThat(user).isEmpty() ;
	}
	
	@Test
	@DisplayName("test save user repository Success")
	@MongoDataFile(value = "user_stats0.json", classType = UserStats.class, collectionName = "UserStats")
	public void testSaveUserStatsSuccess() {
		repository.save(new UserStats(ID, 1, 2, 3));
		
		Optional<UserStats> user = repository.findById(ID) ;
		assertThat(user).isPresent() ;
		UserStats savedUserStats = user.get() ;
		assertThat(savedUserStats.getId()).isEqualTo(ID) ;
	}
	
	@Test
	@DisplayName("test save user repository Success")
	@MongoDataFile(value = "user_stats2.json", classType = UserStats.class, collectionName = "UserStats")
	public void testSaveUserStatsCheckFindAllSuccess() {
		repository.save(new UserStats(ID + "3", 1, 2, 3));
		
		List<UserStats> users = repository.findAll() ;
		assertThat(users).hasSize(3) ;
	}
	
	@Test
	@DisplayName("test find by id repository NotFound")
	@MongoDataFile(value = "user_stats2.json", classType = UserStats.class, collectionName = "UserStats")
	public void testDeleteSuccess() {
		repository.deleteById(ID) ;
		
		Optional<UserStats> user = repository.findById(ID) ;
		assertThat(user).isEmpty() ;
	}
}
