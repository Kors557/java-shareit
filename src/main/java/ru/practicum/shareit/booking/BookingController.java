package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.mapper.BookingMapper;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody RequestBookingDto requestBookingDto) {
        return bookingMapper.mapToBookingDto(bookingService.addBooking(userId, requestBookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto handleBookingApproval(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        return bookingMapper.mapToBookingDto(bookingService.handleBookingApproval(userId, bookingId, approved));
    }

    @GetMapping("{bookingId}")
    public BookingDto getUserBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId) {
        return bookingMapper.mapToBookingDto(bookingService.getUserBookingById(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getAllUserBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @Positive @RequestParam(required = false) Integer from,
                                              @Positive @RequestParam(required = false) Integer size) {
        State stateEnum = State.fromString(state.toUpperCase());
        return bookingService.getAllUserBooking(userId, stateEnum, from, size)
                .stream().map(bookingMapper::mapToBookingDto).toList();
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @Positive @RequestParam(required = false) Integer from,
                                               @Positive @RequestParam(required = false) Integer size) {
        State stateEnum = State.fromString(state.toUpperCase());
        return bookingService.getAllOwnerBooking(ownerId, stateEnum, from, size)
                .stream().map(bookingMapper::mapToBookingDto).toList();
    }
}
