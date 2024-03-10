package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;

import java.util.Collection;

@Service
public interface ItemService {
    ItemDtoIn addItem(long userId, ItemDtoIn itemDto);

    ItemDtoIn updateItem(long userId, long itemId, ItemDtoIn itemDto);

    ItemDto getItemForId(long userId, long itemId);

    Collection<ItemDto> getAllMyItem(long userId);

    Collection<ItemDto> searchForText(String text);

    CommentDtoIn addComment(long userId, long itemId, CommentDtoIn commentDto);
}
