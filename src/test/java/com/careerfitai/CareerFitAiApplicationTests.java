package com.careerfitai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class CareerFitAiApplicationTests {

    @Test
    @DisplayName("Spring Application Context should load successfully")
    void contextLoads() {
        // Verifies bean creation, JPA configuration, and dependency injection
    }
}
