package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String headerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(headerUserId) long userId,
                                          @RequestBody ItemDtoIn itemDtoIn) {
        log.info("Post item, userId = {}", userId);
        return itemClient.addItem(userId, itemDtoIn);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(headerUserId) long userId, @PathVariable long itemId,
                                             @RequestBody ItemDtoIn itemDtoIn) {
        log.info("Patch item {}, userId = {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDtoIn);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItemForId(@RequestHeader(headerUserId) long userId, @PathVariable long itemId) {
        log.info("Get item {}, userId = {}", itemId, userId);
        return itemClient.getItemForId(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllMyItem(@RequestHeader(headerUserId) long userId,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "50") Integer size) {
        log.info("Get all item from user {}, from = {}, size = {}", userId, from, size);
        return itemClient.getAllMyItem(userId, from, size);
    }

    @GetMapping(value = "/search") // items/search?text={text}
    public ResponseEntity<Object> searchForText(@RequestHeader(headerUserId) long userId,
                                                @RequestParam String text,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "50") Integer size) {
        log.info("Search text = '{}', userId = {}, from = {}, size = {}", text, userId, from, size);
        return itemClient.searchForText(text, userId, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")  //POST /items/{itemId}/comment
    public ResponseEntity<Object> addComment(@RequestHeader(headerUserId) long userId,
                                             @PathVariable long itemId, @RequestBody CommentDtoIn commentDto) {
        log.info("Post comment, userId = {}, itemId = {}", userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
