package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Service
public interface ItemService {
    Item addItem(long userId, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto itemDto);

    Item getItemForId(long userId, long itemId);

    Collection<Item> getAllMyItem(long userId);

    Collection<Item> searchForText(String text);
}
