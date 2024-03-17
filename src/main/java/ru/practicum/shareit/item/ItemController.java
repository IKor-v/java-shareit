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
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;

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
    public ItemDtoIn addItem(@RequestHeader(headerUserId) long userId, @Validated(CreateInfo.class) @RequestBody ItemDtoIn itemDtoIn) throws ValidationException {
        ItemDtoIn result = itemService.addItem(userId, itemDtoIn);
        log.info("Добавили вещь с id = " + result.getId());
        return result;
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDtoIn updateItem(@RequestHeader(headerUserId) long userId, @PathVariable long itemId, @Validated @RequestBody ItemDtoIn itemDtoIn) throws ValidationException {
        ItemDtoIn result = itemService.updateItem(userId, itemId, itemDtoIn);
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
    public Collection<ItemDto> getAllMyItem(@RequestHeader(headerUserId) long userId,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "50") Integer size) {
        Collection<ItemDto> result = itemService.getAllMyItem(userId, from, size);
        log.info("Просмотр всех вещей пользователя с id = " + userId);
        return result;
    }

    @GetMapping(value = "/search") // items/search?text={text}
    public Collection<ItemDto> searchForText(@RequestParam String text,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "50") Integer size) {
        return itemService.searchForText(text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")  //POST /items/{itemId}/comment
    public CommentDtoIn addComment(@RequestHeader(headerUserId) long userId, @PathVariable long itemId, @Validated @RequestBody CommentDtoIn commentDto) {
        CommentDtoIn result = itemService.addComment(userId, itemId, commentDto);
        log.info("Комментарий добавлен");
        return result;
    }

}
