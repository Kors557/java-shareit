package ru.practicum.shareit.booking.util;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DateRangeValidatorTest {
    private DateRangeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DateRangeValidator();
        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void testEndDateInPast() {
        RequestBookingDto bookingDto = RequestBookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        assertFalse(validator.isValid(bookingDto, context));
        verify(context).buildConstraintViolationWithTemplate("End date cannot be in the past");
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void testStartDateInPast() {
        RequestBookingDto bookingDto = RequestBookingDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        assertFalse(validator.isValid(bookingDto, context));
        verify(context).buildConstraintViolationWithTemplate("Start date cannot be in the past");
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void testStartDateAfterEndDate() {
        RequestBookingDto bookingDto = RequestBookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        assertFalse(validator.isValid(bookingDto, context));
        verify(context).buildConstraintViolationWithTemplate("Start date cannot be after the end date");
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void testValidDateRange() {
        RequestBookingDto bookingDto = RequestBookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertTrue(validator.isValid(bookingDto, context));
    }

    @Test
    void testNullDates() {
        RequestBookingDto bookingDto = RequestBookingDto.builder().build();
        assertFalse(validator.isValid(bookingDto, context));
        verify(context).buildConstraintViolationWithTemplate("Start and end dates cannot be null");
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void testStartEqualsEnd() {
        RequestBookingDto bookingDto = RequestBookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        assertTrue(validator.isValid(bookingDto, context));
    }
}
