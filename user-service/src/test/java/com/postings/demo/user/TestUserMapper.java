package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.DTO_LASTNAME;
import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.USERNAME;
import static com.postings.demo.user.builder.TestUserBuilder.fullUser;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.mapper.UserMapper;
import com.postings.demo.user.model.User;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
public class TestUserMapper {

	@Autowired
	private UserMapper mapper ;
	
	@Test
	public void mapDtoToUser() {
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setLastName(DTO_LASTNAME);
		
		User user = fullUser() ;
		mapper.mapUpdateUser(dto, user) ;
		
		assertThat(user.getLastName()).isEqualTo(DTO_LASTNAME) ;
		assertThat(user.getId()).isEqualTo(ID) ;
		assertThat(user.getUsername()).isEqualTo(USERNAME) ;
	}
}
