package com.pragma.powerup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
// @Disabled("Configuration issues with placeholders - requires environment
// setup")
class PowerUpApplicationTests {

  @Test
  void contextLoads() {
    // Intentionally empty: this test verifies that the Spring application context
    // loads successfully.
    // Required as a smoke test for application bootstrap; no behavior to assert
    // here. (Sonar: java:S1186)
  }
}
