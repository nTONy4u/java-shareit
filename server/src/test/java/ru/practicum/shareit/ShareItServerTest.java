package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ShareItServerTest {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
        });
    }

    @Test
    void mainMethod_ShouldStartApplication() {
        assertDoesNotThrow(() -> ShareItServer.main(new String[]{}));
    }
}