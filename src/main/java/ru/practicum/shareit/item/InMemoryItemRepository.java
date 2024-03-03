package ru.practicum.shareit.item;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long lastId = 1;

    @Override
    public Item addItem(Item item) {
        Long itemId = getLastId();
        item.setId(itemId);
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        Long itemId = item.getId();
        if (items.containsKey(itemId)) {
            items.put(itemId, item);
            return items.get(itemId);
        } else {
            throw new NotFoundException("Такой вещи не найдено.");
        }

    }

    @Override
    public Item getItemForId(long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException("Такой вещи не найдено.");
        }
    }

    @Override
    public Collection<Item> getAllMyItem(User user) {
        Collection<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == user) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public Collection<Item> searchForText(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text) || (item.getDescription().toLowerCase().contains(text))))
                .filter(item -> (item.isAvailable()))
                .collect(Collectors.toList());
    }

    private long getLastId() {
        return lastId++;
    }
}
