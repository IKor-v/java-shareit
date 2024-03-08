package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.Collection;

@Service
public interface BookingService {
    BookingDtoOut addBooking(BookingDto bookingDto, Long userId);

    BookingDtoOut bookingApproved(Long userId, Long bookingId, Boolean approved);

    BookingDtoOut getBookingById(Long userId, Long bookingId);

    Collection<BookingDtoOut> getAllBookingByUser(Long userId, String state);

    Collection<BookingDtoOut> getAllBookingByOwner(Long userId, String state);
}
