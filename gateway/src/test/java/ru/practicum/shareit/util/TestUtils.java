package ru.practicum.shareit.util;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.lang.reflect.Field;

public class TestUtils {

    public static void setRestTemplate(BaseClient client, RestTemplate restTemplate) {
        try {
            Field restField = BaseClient.class.getDeclaredField("rest");
            restField.setAccessible(true);
            restField.set(client, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set rest template", e);
        }
    }
}