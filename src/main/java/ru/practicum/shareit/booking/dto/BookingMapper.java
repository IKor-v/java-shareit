package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {

        return new BookingDto(
                booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        return new BookingDtoOut(
                booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }


    public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
        Booking result = new Booking();
        result.setStart(LocalDateTime.parse(bookingDto.getStart()));
        result.setEnd(LocalDateTime.parse(bookingDto.getEnd()));
        result.setItem(item);
        result.setBooker(booker);
        result.setStatus(bookingDto.getStatus());
        return result;
    }
}
