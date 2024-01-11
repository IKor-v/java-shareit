package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;

@Primary
@Service
public class ItemServiceImp implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final long ANY_ID = 0;

    @Autowired
    public ItemServiceImp(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Item addItem(long userId, ItemDto itemDto) {
        validationItem(itemDto);
        return itemStorage.addItem(ItemMapper.toItem(itemDto, userService.getUser(userId), ANY_ID));
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        Item oldItem = itemStorage.getItemForId(itemId);
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
        return itemStorage.updateItem(ItemMapper.toItem(itemDto, userService.getUser(userId), itemId));
    }

    @Override
    public Item getItemForId(long userId, long itemId) {
        return itemStorage.getItemForId(itemId);
    }

    @Override
    public Collection<Item> getAllMyItem(long userId) {
        return itemStorage.getAllMyItem(userService.getUser(userId));
    }

    @Override
    public Collection<Item> searchForText(String text) {
        if ((text == null) || (text.isBlank())) {
            return new ArrayList<>();
        }
        return itemStorage.searchForText(text.toLowerCase());
    }

    private boolean validationItem(ItemDto itemDto) {
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
            return true;
        }
        throw new ValidationException(message);
    }
}
