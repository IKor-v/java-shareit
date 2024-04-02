package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

@UtilityClass
public class BookingMapper {

    public ItemDto.BookingDto toBookingDto(Booking booking) {

        return new ItemDto.BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }

    public BookingDtoOut toBookingDtoOut(Booking booking) {
        String start = booking.getStart().toString();
        String end = booking.getEnd().toString();
        if (start.length() < 19) {
            start = start + ":00";
        }
        if (end.length() < 19) {
            end = end + ":00";
        }
        return new BookingDtoOut(
                booking.getId(),
                start,
                end,
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public Booking toBookingFromBookingDtoIn(BookingDtoIn bookingDtoIn, User booker, Item item) {
        Booking result = new Booking();
        result.setStart(LocalDateTime.parse(bookingDtoIn.getStart()));
        result.setEnd(LocalDateTime.parse(bookingDtoIn.getEnd()));
        result.setItem(item);
        result.setBooker(booker);
        result.setStatus(BookingStatus.WAITING);
        return result;
    }
}
