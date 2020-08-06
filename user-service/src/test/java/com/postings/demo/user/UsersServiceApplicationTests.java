package com.postings.demo.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
class UsersServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
