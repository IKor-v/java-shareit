package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.DatabaseUserRepository;
import ru.practicum.shareit.user.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
@Primary
public class ItemServiceImpDb implements ItemService {
    private final DatabaseItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final DatabaseUserRepository userRepository;
    private final CommentRepository commentRepository;


    @Autowired
    public ItemServiceImpDb(DatabaseItemRepository itemRepository, BookingRepository bookingRepository,
                            DatabaseUserRepository userRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public static ItemDto getLastAndNextBooking(ItemDto itemDto, BookingRepository bookingRepository1) {
        Collection<BookingStatus> st = Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED);
        Collection<Booking> bookings = bookingRepository1.findByItemIdAndStatusInOrderByStartDesc(itemDto.getId(), st);
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        for (Booking booking : bookings) {
            if (((lastBooking == null) || (booking.getEnd().isAfter(lastBooking.getEnd()))) && ((booking.getEnd().isBefore(now))
                    || ((booking.getStart().isBefore(now)) && (booking.getEnd().isAfter(now))))) {
                lastBooking = booking;
            } else if (((nextBooking == null) || (booking.getStart().isBefore(nextBooking.getStart()))) && ((booking.getStart().isAfter(now)))) {
                nextBooking = booking;
            }
        }
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
        return itemDto;
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validationItem(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти хозяина вещи, данные не верны"));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не удалось найти такую вещь, данные не верны"));
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
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, userRepository.getById(userId), itemId)));
    }

    @Override
    public ItemDto getItemForId(long userId, long itemId) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не удалось найти такую вещь, данные не верны.")));
        Collection<Comment> comments = commentRepository.findByItemIdOrderById(itemId);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (itemDto.getOwner().getId().equals(userId)) {
            return getLastAndNextBooking((itemDto), bookingRepository);
        }
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllMyItem(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id = " + userId));
        Collection<Comment> comments = commentRepository.findByItemOwnerIdOrderById(userId);
        Collection<CommentDto> com = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        Collection<ItemDto> result2 = new ArrayList<>();

        Collection<Item> result = itemRepository.findByOwner(user);
        Collection<ItemDto> result1 = result.stream()
                .map(ItemMapper::toItemDto)
                .map(itemDto -> getLastAndNextBooking(itemDto, bookingRepository))
                .collect(Collectors.toList());

        for (ItemDto itemDto : result1) {
            Collection<CommentDto> waiting = new ArrayList<>();
            for (CommentDto commentDto : com) {
                if (itemDto.getId().equals(commentDto.getItemId())) {
                    ;
                    waiting.add(commentDto);
                }
            }
            if (!waiting.isEmpty()) {
                itemDto.setComments(waiting);
            }
            result2.add(itemDto);
        }
        return result2;
    }

    @Override
    public Collection<ItemDto> searchForText(String text) {
        if ((text == null) || (text.isBlank())) {
            return Collections.emptyList();
        }
        return itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не удалось найти предмет с id = " + itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id = " + userId));
        commentDto.setAuthorId(userId);
        commentDto.setAuthorName(user.getName());
        commentDto.setItemId(itemId);
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(itemId, userId, BookingStatus.APPROVED);
        if ((booking != null) && (booking.getEnd().isBefore/*(commentDto.getCreated()))){*/(LocalDateTime.parse(commentDto.getCreated())))) {
            return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, user, item)));
        }
        throw new ValidationException("Комментарий можно оставить только после окончания бронирования");
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
