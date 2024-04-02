package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;

import java.util.List;

public interface ItemService {
    ItemDtoIn addItem(long userId, ItemDtoIn itemDto);

    ItemDtoIn updateItem(long userId, long itemId, ItemDtoIn itemDto);

    ItemDto getItemForId(long userId, long itemId);

    List<ItemDto> getAllMyItem(long userId, Integer from, Integer size);

    List<ItemDto> searchForText(String text, Integer from, Integer size);

    CommentDtoIn addComment(long userId, long itemId, CommentDtoIn commentDto);
}
