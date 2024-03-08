package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.groupvalid.CreateInfo;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = CreateInfo.class)
    private String name;
    @NotBlank(groups = CreateInfo.class)
    private String description;
    @NotNull(groups = CreateInfo.class)
    private Boolean available;
    private UserDto owner;
    private ItemRequest request;

    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDto> comments;

}
