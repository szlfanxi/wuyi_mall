package com.wuyimall.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private com.wuyimall.repository.UserRepository userRepository;

    @Test
    void registerEndpoint_shouldReturnSuccess() {
        String url = "http://localhost:" + port + "/api/users/register";
        String username = "http_test_" + UUID.randomUUID();
        Map<String, String> payload = Map.of(
                "username", username,
                "password", "password123",
                "nickname", "tester"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, payload, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("success", true);

        // 清理测试数据
        var created = userRepository.findByUsername(username);
        if (created != null) {
            userRepository.delete(created);
        }
    }
}
