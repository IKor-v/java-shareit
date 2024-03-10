package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
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

    public Item toItem(ItemDtoIn itemDto, User owner) {
        Item result = new Item();
        result.setName(itemDto.getName());
        result.setDescription(itemDto.getDescription());
        result.setAvailable(itemDto.getAvailable());
        result.setOwner(owner);
        result.setRequest(null);
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
        return new ItemDtoIn(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

}
