package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.fullUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.postings.demo.user.model.UserStats;
import com.postings.demo.user.repository.UserStatsRepository;
import com.postings.demo.user.service.UserStatsService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
public class TestUserStatsService {
	
	@Autowired
	private UserStatsService service ;
	
	@MockBean
	private UserStatsRepository repository ;
	
	private UserStats stats ;
	
	@BeforeEach
	public void setUp() {
		stats = new UserStats(ID, 1, 2, 3) ;
	}
	
	@Test
	@DisplayName("find user by id Success")
	public void testFindUserByIdSuccess() {
		doReturn(Optional.of(stats)).when(repository).findById(ID) ;
		Optional<UserStats> stats = service.findById(ID) ;
		assertThat(stats).withFailMessage("stats had to be found").isPresent() ;
	}
	
	@Test
	@DisplayName("find user by id Not Found")
	public void testFindUserByIdNotFound() {
		doReturn(Optional.empty()).when(repository).findById(ID) ;
		Optional<UserStats> stats = service.findById(ID) ;
		assertThat(stats).withFailMessage("User had not to be found").isEmpty() ;
	}
	
	@Test
	@DisplayName("create user Success")
	public void testCreateUserSuccess() {
		doReturn(stats).when(repository).save(any()) ;
		UserStats savedStats = service.save(stats);
		assertThat(savedStats).withFailMessage("User must be not null").isNotNull() ;
		assertThat(savedStats.getId()).isNotNull().isEqualTo(ID) ;
		assertThat(savedStats.getPosts()).isNotNull().isEqualTo(1) ;
		assertThat(savedStats.getFollowers()).isNotNull().isEqualTo(2) ;
		assertThat(savedStats.getFollowing()).isNotNull().isEqualTo(3) ;
	}
	
	@Test
	@DisplayName("delete user Success")
	public void testDeleteUserSuccess() {
		doNothing().when(repository).deleteById(ID) ;
		service.delete(ID) ;
		verify(repository).deleteById(ID);
	}
}
