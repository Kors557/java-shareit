package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ShareItTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainTest() {
		assertDoesNotThrow(() -> ShareItServer.main(new String[]{}));
	}
}
