package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private final String headerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(headerUserId) Long userId, @RequestBody RequestDto itemRequestDto) {
        log.info("Post request userId = {}", userId);
        return requestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsFromUser(@RequestHeader(headerUserId) Long userId) {
        log.info("Get request userId = {}", userId);
        return requestClient.getRequestsFromUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsFromOtherUser(@RequestHeader(headerUserId) long userId,
                                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(required = false, defaultValue = "50") Integer size) {
        log.info("Get request userId = {}, from = {}, size = {}", userId, from, size);
        return requestClient.getRequestsFromOtherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(headerUserId) long userId, @PathVariable Long requestId) {
        log.info("Get request by id = {}, userId = {}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
