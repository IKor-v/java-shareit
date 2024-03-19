package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;
    private final String headerUserId = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    //POST /requests
    public ItemRequestDto addRequest(@RequestHeader(headerUserId) Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDto result = requestService.addRequest(userId, itemRequestDto);
        log.info("Добавлен запрос вещи с id = " + result.getId());
        return result;
    }

    @GetMapping
    //GET /requests
    public List<ItemRequestDtoOut> getRequestsFromUser(@RequestHeader(headerUserId) Long userId) {
        List<ItemRequestDtoOut> result = requestService.getRequestsFromUser(userId);
        log.info("Получен список личных запросов");
        return result;
    }

    @GetMapping("/all")
    //GET /requests/all?from={from}&size={size}
    public List<ItemRequestDtoOut> getRequestsFromOtherUser(@RequestHeader(headerUserId) long userId,
                                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                                            @RequestParam(required = false, defaultValue = "50") Integer size) {
        List<ItemRequestDtoOut> result = requestService.getRequestsFromOtherUser(userId, from, size);
        log.info("Получен список чужих запросов");
        return result;
    }

    @GetMapping("/{requestId}")
    //GET /requests/{requestId}
    public ItemRequestDtoOut getRequestById(@RequestHeader(headerUserId) long userId, @PathVariable Long requestId) {
        ItemRequestDtoOut result = requestService.getRequestById(userId, requestId);
        log.info("Просмотрен запрос с id = " + result.getId());
        return result;
    }
}
