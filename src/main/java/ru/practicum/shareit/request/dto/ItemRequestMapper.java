package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated()
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest result = new ItemRequest();
        result.setDescription(itemRequestDto.getDescription());
        result.setCreated(itemRequestDto.getCreated());
        result.setRequestor(user);
        return result;
    }

    public ItemRequestDtoOut toItemRequestDtoOut(ItemRequest itemRequest, List<ItemDtoIn> items) {
        return new ItemRequestDtoOut(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                items
        );
    }


}
