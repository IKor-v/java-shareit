package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final long anyId = 0;

    @Autowired
    public ItemServiceImp(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validationItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.addItem(ItemMapper.toItem(itemDto, userRepository.getUser(userId), anyId)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item oldItem = itemRepository.getItemForId(itemId);
        if (userId != oldItem.getOwner().getId()) {
            throw new NotFoundException("Нельзя обновить информацию о вещи другого человека");
        }
        if (itemDto.getName() == null) {
            itemDto.setName(oldItem.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(oldItem.isAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(itemDto, userRepository.getUser(userId), itemId)));
    }

    @Override
    public ItemDto getItemForId(long userId, long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemForId(itemId));
    }

    @Override
    public Collection<ItemDto> getAllMyItem(long userId) {
        Collection<Item> result = itemRepository.getAllMyItem(userRepository.getUser(userId));
        return result.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchForText(String text) {
        if ((text == null) || (text.isBlank())) {
            return Collections.emptyList();
        }
        return itemRepository.searchForText(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validationItem(ItemDto itemDto) {
        String message = "Ошибка валидации вещи: ";
        if (itemDto == null) {
            message += "переданно пустое тело.";
            throw new ValidationException(message);
        }
        if (itemDto.getAvailable() == null) {
            message += "не указан статус.";
        } else if ((itemDto.getName() == null) || (itemDto.getName().isBlank())) {
            message += "не указано название.";
        } else if (itemDto.getDescription() == null) {
            message += "не заполненно описание.";
        } else {
            return;
        }
        throw new ValidationException(message);
    }
}
