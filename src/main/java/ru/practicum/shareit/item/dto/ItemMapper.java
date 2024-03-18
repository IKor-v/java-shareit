package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        ItemDto result = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                UserMapper.toUserDto(item.getOwner()),
                null,
                null,
                null,
                new ArrayList<>()
        );
        if (item.getRequest() != null) {
            result.setRequest(ItemRequestMapper.toItemRequestDto(item.getRequest()));
        }
        return result;
    }

    public Item toItem(ItemDtoIn itemDto, User owner, ItemRequest itemRequest) {
        Item result = new Item();
        result.setName(itemDto.getName());
        result.setDescription(itemDto.getDescription());
        result.setAvailable(itemDto.getAvailable());
        result.setOwner(owner);
        if (itemRequest != null) {
            result.setRequest(itemRequest);
        }
        if (itemDto.getId() != null) {
            result.setId(itemDto.getId());
        }
        return result;
    }

    public Item toItem(ItemDtoIn itemDto, User owner, Long itemId) {
        Item result = new Item();

        result.setId(itemId);
        result.setName(itemDto.getName());
        result.setDescription(itemDto.getDescription());
        result.setAvailable(itemDto.getAvailable());
        result.setOwner(owner);
        result.setRequest(null);
        return result;
    }

    public ItemDtoIn toItemDtoIn(Item item) {
        ItemRequest itemRequest = item.getRequest();
        Long requestId = null;
        if (itemRequest != null) {
            requestId = itemRequest.getId();
        }
        return new ItemDtoIn(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                requestId
        );
    }

}
