package com.unimeet.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests extends TestBase {

	@Test
	@DisplayName("Spring Context 로드 테스트")
	void contextLoads() {
		// Spring Boot 애플리케이션이 정상적으로 시작되는지 확인
	}

}
