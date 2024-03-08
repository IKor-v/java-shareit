package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
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
    }

    public static Item toItem(ItemDto itemDto, User owner, long itemId) {
        Item result = new Item();
        result.setId(itemId);
        result.setName(itemDto.getName());
        result.setDescription(itemDto.getDescription());
        result.setAvailable(itemDto.getAvailable());
        result.setOwner(owner);
        result.setRequest(null);
        return result;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item result = new Item();
        result.setName(itemDto.getName());
        result.setDescription(itemDto.getDescription());
        result.setAvailable(itemDto.getAvailable());
        result.setOwner(owner);
        result.setRequest(null);
        return result;
    }
}
