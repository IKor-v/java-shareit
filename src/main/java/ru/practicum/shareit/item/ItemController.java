package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) throws ValidationException {
        Item result = itemService.addItem(userId, itemDto);
        log.info("Добавили вещь с id = " + result.getId());
        return result;
    }

    @PatchMapping(value = "/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @Valid @RequestBody ItemDto itemDto) throws ValidationException {
        Item result = itemService.updateItem(userId, itemId, itemDto);
        log.info("Обновили вещь с id = " + result.getId());
        return result;
    }

    @GetMapping(value = "/{itemId}")
    public Item getItemForId(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        Item result = itemService.getItemForId(userId, itemId);
        log.info("Просмотр вещи с id = " + result.getId());
        return result;
    }

    @GetMapping
    public Collection<Item> getAllMyItem(@RequestHeader("X-Sharer-User-Id") long userId) {
        Collection<Item> result = itemService.getAllMyItem(userId);
        log.info("Просмотр всех вещей пользователя с id = " + userId);
        return result;
    }

    @GetMapping(value = "/search") // items/search?text={text}
    public Collection<Item> searchForText(@RequestParam String text) {
        return itemService.searchForText(text);
    }

}
