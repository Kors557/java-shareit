package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class RequestBookingDtoTest {
    @Test
    void testBuilder() {
        RequestBookingDto bookingDto = RequestBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 10, 16, 10, 0, 0))
                .end(LocalDateTime.of(2024, 10, 18, 12, 0, 0))
                .build();

        assertEquals(1L, bookingDto.getItemId());
        assertEquals(LocalDateTime.of(2024, 10, 16, 10, 0, 0), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2024, 10, 18, 12, 0, 0), bookingDto.getEnd());
    }

    @Test
    void testEquals() {
        RequestBookingDto bookingDto1 = RequestBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 10, 16, 10, 0, 0))
                .end(LocalDateTime.of(2024, 10, 18, 12, 0, 0))
                .build();

        RequestBookingDto bookingDto2 = RequestBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 10, 16, 10, 0, 0))
                .end(LocalDateTime.of(2024, 10, 18, 12, 0, 0))
                .build();

        assertEquals(bookingDto1, bookingDto2);
    }

    @Test
    void testNotEquals() {
        RequestBookingDto bookingDto1 = RequestBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 10, 16, 10, 0, 0))
                .end(LocalDateTime.of(2024, 10, 18, 12, 0, 0))
                .build();

        RequestBookingDto bookingDto2 = RequestBookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.of(2024, 10, 16, 10, 0, 0))
                .end(LocalDateTime.of(2024, 10, 18, 12, 0, 0))
                .build();

        assertNotEquals(bookingDto1, bookingDto2);
    }

    @Test
    void testHashCode() {
        RequestBookingDto bookingDto1 = RequestBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 10, 16, 10, 0, 0))
                .end(LocalDateTime.of(2024, 10, 18, 12, 0, 0))
                .build();

        RequestBookingDto bookingDto2 = RequestBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 10, 16, 10, 0, 0))
                .end(LocalDateTime.of(2024, 10, 18, 12, 0, 0))
                .build();

        assertEquals(bookingDto1.hashCode(), bookingDto2.hashCode());
    }
}
