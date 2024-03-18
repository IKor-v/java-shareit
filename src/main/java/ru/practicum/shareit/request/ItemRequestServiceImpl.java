package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public static void checkPageableInfo(Integer from, Integer size) {
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Переданный неверные данные индекса первого элемента или количество элементов.");
        }
    }

    public static ItemRequestDtoOut getRequestDtoOut(ItemRequest itemRequest, List<Item> items) {
        if (itemRequest == null) {
            return null;
        }

        List<ItemDtoIn> resultItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getRequest().getId().equals(itemRequest.getId())) {
                resultItems.add(ItemMapper.toItemDtoIn(item));
            }
        }
        return ItemRequestMapper.toItemRequestDtoOut(itemRequest, resultItems);
    }

    @Override
    public ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto) {
        validationBooking(itemRequestDto);
        itemRequestDto.setCreated(LocalDateTime.now());
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id =" + userId));
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, requestor)));
    }

    @Override
    public List<ItemRequestDtoOut> getRequestsFromUser(long userId) {
        checkUserExist(userId);
        List<ItemRequest> result = requestRepository.findAllByIdOrderByCreatedDesc(userId);
        List<Item> allItem = itemRepository.findAllByRequestNotNull();

        return result.stream()
                .map(itemRequest -> getRequestDtoOut(itemRequest, allItem))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> getRequestsFromOtherUser(long userId, Integer from, Integer size) {
        checkPageableInfo(from, size);
        checkUserExist(userId);

        Pageable pageable = PageRequest.of(from / size, size);

        List<ItemRequest> result = requestRepository.findAllByIdNotOrderByCreatedDesc(userId, pageable);
        List<Item> allItem = itemRepository.findAllByRequestNotNull();
        return result.stream()
                .map(itemRequest -> getRequestDtoOut(itemRequest, allItem))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOut getRequestById(long userId, Long requestId) {
        checkUserExist(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Не найден запрос с id = " + requestId));
        List<ItemDtoIn> items = itemRepository.findAllByRequestId(requestId).stream().map(ItemMapper::toItemDtoIn).collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDtoOut(itemRequest, items);
    }

    private void checkUserExist(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id =" + userId));
    }

    private void validationBooking(ItemRequestDto itemRequestDto) {
        String message = "Ошибка валидации вещи: ";
        if (itemRequestDto == null) {
            message += "переданно пустое тело запроса.";
            throw new ValidationException(message);
        } else if ((itemRequestDto.getDescription() == null) || (itemRequestDto.getDescription().isBlank())) {
            message += "Описание запроса не может быть пустым.";
            throw new ValidationException(message);
        }
        return;
    }

}
