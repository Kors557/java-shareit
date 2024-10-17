package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.State;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingClientTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private BookingClient bookingClient;

    @Test
    void getAllUserBooking() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getAllUserBooking(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());
    }

    @Test
    void getAllOwnerBooking() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getAllOwnerBooking(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());
    }

    @Test
    void addBooking() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.addBooking(anyLong(), any(RequestBookingDto.class)))
                .thenReturn(ResponseEntity.ok().build());
    }

    @Test
    void getUserBookingById() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getUserBookingById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());
    }

    @Test
    void handleBookingApproval() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.handleBookingApproval(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());
    }
}
