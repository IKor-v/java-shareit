package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final String headerUserId = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoOut addBooking(@RequestHeader(headerUserId) long userId, @RequestBody BookingDtoIn bookingDtoIn) {
        BookingDtoOut result = bookingService.addBooking(bookingDtoIn, userId);
        log.info("Бронирование создано");
        return result;
    }

    @PatchMapping("/{bookingId}") //PATCH /bookings/{bookingId}?approved={approved}
    public BookingDtoOut bookingApproved(@RequestHeader(headerUserId) long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        BookingDtoOut result = bookingService.bookingApproved(userId, bookingId, approved);
        log.info("Запрос бронирования решён");
        return result;
    }

    @GetMapping("/{bookingId}")  //GET /bookings/{bookingId}
    public BookingDtoOut getBookingById(@RequestHeader(headerUserId) long userId, @PathVariable long bookingId) {
        BookingDtoOut result = bookingService.getBookingById(userId, bookingId);
        log.info("Просмотренна информация по бронированию с id = " + bookingId);
        return result;
    }

    @GetMapping   //GET /bookings?state={state}
    public Collection<BookingDtoOut> getAllBookingByBooker(@RequestHeader(headerUserId) long userId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        Collection<BookingDtoOut> result = bookingService.getAllBookingByBooker(userId, state);
        log.info("Просмотрен список всех бронирований пользователя с id =" + userId);
        return result;
    }

    @GetMapping("/owner")  //GET /bookings/owner?state={state}
    public Collection<BookingDtoOut> getAllBookingByOwner(@RequestHeader(headerUserId) long userId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        Collection<BookingDtoOut> result = bookingService.getAllBookingByOwner(userId, state);
        log.info("Просмотрен список бронирований для всех вещей пользователя с id = " + userId);
        return result;
    }
}
