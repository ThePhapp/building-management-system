package com.javaweb.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDtoSerializationRegressionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serializedUserNeverContainsPasswordPropertyOrValue() throws Exception {
        UserDTO user = new UserDTO();
        user.setUserName("security-test-admin");
        user.setPassword("must-never-leave-the-server");

        String json = objectMapper.writeValueAsString(user);
        JsonNode document = objectMapper.readTree(json);

        assertThat(document.has("password"))
                .as("UserDTO JSON must not expose a password property")
                .isFalse();
        assertThat(json)
                .as("UserDTO JSON must not expose the password value")
                .doesNotContain("must-never-leave-the-server");
    }
}
