package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.erorr.exception.AccessDeniedException;
import ru.practicum.shareit.erorr.exception.EntityNotFoundException;
import ru.practicum.shareit.erorr.exception.NotAuthorizedException;
import ru.practicum.shareit.erorr.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final String LAST_BOOKING = "last";
    private static final String NEXT_BOOKING = "next";
    private final BookingRepository bookingRepository;
    private final UserService userServiceImpl;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking addBooking(long bookerId, RequestBookingDto requestBookingDto) {
        log.info("Adding booking: {}", requestBookingDto);
        User booker = userServiceImpl.getUser(bookerId);
        Item item = itemRepository.findById(requestBookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        Booking booking = bookingMapper.mapToBooking(requestBookingDto);
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }
        if (item.getOwner().getId() == bookerId) {
            throw new NotAuthorizedException("User is not the owner of this booking");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(WAITING);
        Booking saveBooking = bookingRepository.save(booking);
        booking.setId(saveBooking.getId());
        log.info("Booking saved: {}", saveBooking);
        return saveBooking;
    }

    @Override
    @Transactional
    public Booking handleBookingApproval(long ownerId, long bookingId, boolean approved) {
        log.info("Handling booking approval for user ID: {}, bookingId: {}", ownerId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found" + bookingId));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new AccessDeniedException("You are not allowed to approve this booking");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("The status has already been approved");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : REJECTED);
        Booking saveBooking = bookingRepository.save(booking);
        log.info("The booking status is set to: {}", booking.getStatus());
        return saveBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getUserBookingById(long bookerId, long bookingId) {
        log.info("Get booking ID {} for user ID {} ", bookingId, bookerId);
        return bookingRepository.getBookingIfOwnedByUser(bookingId, bookerId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllUserBooking(long userId, State state, Integer from, Integer size) {
        log.info("Get all bookings for user ID {} with state {}", userId, state);
        userServiceImpl.getUser(userId);
        Pageable pageable = (from != null && size != null)
                ? PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                : Pageable.unpaged();
        return switch (state) {
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, REJECTED);
            case CURRENT -> bookingRepository.findAllByBookerIdAndCurrentTime(userId,
                    LocalDateTime.now());
            case ALL -> {
                Page<Booking> bookingsPage = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                yield bookingsPage.getContent();
            }
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId,
                    LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                    LocalDateTime.now());
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllOwnerBooking(long ownerId, State state, Integer from, Integer size) {
        log.info("Get all bookings for owner ID {} with state {}", ownerId, state);
        userServiceImpl.getUser(ownerId);
        Pageable pageable = (from != null && size != null)
                ? PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                : Pageable.unpaged();
        return switch (state) {
            case WAITING -> bookingRepository.findAllByOwnerIdAndStatus(ownerId, WAITING);
            case REJECTED -> bookingRepository.findAllByOwnerIdAndStatus(ownerId, REJECTED);
            case CURRENT -> bookingRepository.findAllByOwnerIdAndCurrentTime(ownerId, LocalDateTime.now());
            case ALL -> {
                Page<Booking> bookingsPage = bookingRepository.findAllByOwnerIdOrderByStartDesc(ownerId, pageable);
                yield bookingsPage.getContent();
            }
            case PAST -> bookingRepository.findAllByOwnerIdAndEndBefore(ownerId,
                    LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByOwnerIdAndStartAfter(ownerId,
                    LocalDateTime.now());
        };
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoForItem getBookingForItem(long itemId, String bookingType) {
        log.info("Get booking for item ID {} with time period {}", itemId, bookingType);
        Booking bookingForItem = switch (bookingType) {
            case LAST_BOOKING -> bookingRepository.findLastBookingForItem(itemId, LocalDateTime.now(),
                    REJECTED).orElse(null);
            case NEXT_BOOKING -> bookingRepository.findNextBookingForItem(itemId, LocalDateTime.now(),
                    REJECTED).orElse(null);
            default -> null;
        };
        return bookingMapper.mapToBookingDtoForItem(bookingForItem);
    }

    public Map<Long, List<Booking>> getBookingsForItems(List<Long> itemIds) {
        List<Booking> bookings = bookingRepository.findByItemIdIn(itemIds, REJECTED);
        return bookings.stream().collect(Collectors.groupingBy(Booking::getItemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsForComment(long itemId, long bookerId) {
        log.info("Get booking for item ID {} for comment users id {}", itemId, bookerId);
        return bookingRepository.findBookingForComment(itemId, bookerId, LocalDateTime.now());
    }
}
