package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.groupvalid.CreateInfo;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private ItemRequestDto request;

    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDto> comments;


    @Data
    public static class BookingDto {
        private final Long id;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final Long bookerId;
    }
}


