package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Service
public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemForId(long userId, long itemId);

    Collection<ItemDto> getAllMyItem(long userId);

    Collection<ItemDto> searchForText(String text);
}
