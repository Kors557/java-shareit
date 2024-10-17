package ru.practicum.shareit.erorr.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotAuthorizedExceptionTest {

    @Test
    void testConstructor() {
        String message = "Unauthorized access!";
        NotAuthorizedException exception = new NotAuthorizedException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testGetStackTrace() {
        NotAuthorizedException exception = new NotAuthorizedException("Test exception");
        assertNotNull(exception.getStackTrace());
    }

    @Test
    void testGetCause() {
        NotAuthorizedException exception = new NotAuthorizedException("Test exception");
        assertNull(exception.getCause());
    }
}
