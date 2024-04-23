package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto);


    List<ItemRequestDtoOut> getRequestsFromUser(long userId);

    List<ItemRequestDtoOut> getRequestsFromOtherUser(long userId, Integer from, Integer size);


    ItemRequestDtoOut getRequestById(long userId, Long requestId);
}
