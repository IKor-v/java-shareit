package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.Collection;

public interface BookingService {
    BookingDtoOut addBooking(BookingDtoIn bookingDto, Long userId);

    BookingDtoOut bookingApproved(Long userId, Long bookingId, Boolean approved);

    BookingDtoOut getBookingById(Long userId, Long bookingId);

    Collection<BookingDtoOut> getAllBookingByBooker(Long userId, String state, Integer from, Integer size);

    Collection<BookingDtoOut> getAllBookingByOwner(Long userId, String state, Integer from, Integer size);
}
