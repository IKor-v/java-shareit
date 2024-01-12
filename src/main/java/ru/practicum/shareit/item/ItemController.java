package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.groupvalid.CreateInfo;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ValidationException;
import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final String headerUserId = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @PostMapping
    public ItemDto addItem(@RequestHeader(headerUserId) long userId, @Validated(CreateInfo.class) @RequestBody ItemDto itemDto) throws ValidationException {
        ItemDto result = itemService.addItem(userId, itemDto);
        log.info("Добавили вещь с id = " + result.getId());
        return result;
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto updateItem(@RequestHeader(headerUserId) long userId, @PathVariable long itemId, @Validated @RequestBody ItemDto itemDto) throws ValidationException {
        ItemDto result = itemService.updateItem(userId, itemId, itemDto);
        log.info("Обновили вещь с id = " + result.getId());
        return result;
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItemForId(@RequestHeader(headerUserId) long userId, @PathVariable long itemId) {
        ItemDto result = itemService.getItemForId(userId, itemId);
        log.info("Просмотр вещи с id = " + result.getId());
        return result;
    }

    @GetMapping
    public Collection<ItemDto> getAllMyItem(@RequestHeader(headerUserId) long userId) {
        Collection<ItemDto> result = itemService.getAllMyItem(userId);
        log.info("Просмотр всех вещей пользователя с id = " + userId);
        return result;
    }

    @GetMapping(value = "/search") // items/search?text={text}
    public Collection<ItemDto> searchForText(@RequestParam String text) {
        return itemService.searchForText(text);
    }

}
