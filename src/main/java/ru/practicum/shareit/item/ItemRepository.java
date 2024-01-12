package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemForId(long itemId);

    Collection<Item> getAllMyItem(User user);

    Collection<Item> searchForText(String text);
}
