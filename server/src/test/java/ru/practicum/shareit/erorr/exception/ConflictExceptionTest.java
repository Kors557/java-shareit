package ru.practicum.shareit.erorr.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConflictExceptionTest {

    @Test
    void testConstructor() {
        String message = "Conflict occurred!";
        ConflictException exception = new ConflictException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testGetStackTrace() {
        ConflictException exception = new ConflictException("Test exception");
        assertNotNull(exception.getStackTrace());
    }

    @Test
    void testGetCause() {
        ConflictException exception = new ConflictException("Test exception");
        assertNull(exception.getCause());
    }
}
