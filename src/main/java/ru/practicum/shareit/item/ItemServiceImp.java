package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;


    @Autowired
    public ItemServiceImp(ItemRepository itemRepository, BookingRepository bookingRepository,
                          UserRepository userRepository, CommentRepository commentRepository,
                          ItemRequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    public static ItemDto getLastAndNextBooking(ItemDto itemDto, Collection<Booking> bookings, boolean isSort) {
        if (bookings == null) {
            return null;
        }
        Collection<Booking> bookings1;
        if (!isSort) {
            bookings1 = bookings.stream()
                    .filter(c -> c.getItem().getId().equals(itemDto.getId()))
                    .collect(Collectors.toList());
        } else {
            bookings1 = bookings;
        }
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        for (Booking booking : bookings1) {
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
    @Transactional
    public ItemDtoIn addItem(long userId, ItemDtoIn itemDtoIn) {
        validationItem(itemDtoIn);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти хозяина вещи, данные не верны"));
        Long requestId = itemDtoIn.getRequestId();
        ItemRequest itemRequest = null;
        if (requestId != null) {
            itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Не удалось найти запрос с id = " + requestId));
        }
        return ItemMapper.toItemDtoIn(itemRepository.save(ItemMapper.toItem(itemDtoIn, owner, itemRequest)));
    }

    @Override
    @Transactional
    public ItemDtoIn updateItem(long userId, long itemId, ItemDtoIn itemDto) {
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
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        return ItemMapper.toItemDtoIn(itemRepository.save(ItemMapper.toItem(itemDto,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти такого пользователя, данные не верны.")), itemId)));
    }

    @Override
    public ItemDto getItemForId(long userId, long itemId) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не удалось найти такую вещь, данные не верны.")));
        Collection<Comment> comments = commentRepository.findByItemIdOrderById(itemId);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (itemDto.getOwner().getId().equals(userId)) {
            return getLastAndNextBooking((itemDto),
                    bookingRepository.findByItemIdAndStatusInOrderByStartDesc(itemDto.getId(), Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED)), true);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllMyItem(long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id = " + userId));
        List<Comment> comments = commentRepository.findByItemOwnerIdOrderById(userId);
        List<CommentDto> com = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        List<ItemDto> result2 = new ArrayList<>();

        List<Booking> bookings = bookingRepository.findByStatusInOrderByStartDesc(Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED));

        ItemRequestServiceImpl.checkPageableInfo(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> result = itemRepository.findByOwnerId(user.getId(), pageable);
        List<ItemDto> result1 = result.stream()
                .map(ItemMapper::toItemDto)
                .map(itemDto -> getLastAndNextBooking(itemDto, bookings, false))
                .collect(Collectors.toList());

        for (ItemDto itemDto : result1) {
            List<CommentDto> waiting = new ArrayList<>();
            for (CommentDto commentDto : com) {
                if (itemDto.getId().equals(commentDto.getItemId())) {
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
    public List<ItemDto> searchForText(String text, Integer from, Integer size) {
        if ((text == null) || (text.isBlank())) {
            return Collections.emptyList();
        }
        ItemRequestServiceImpl.checkPageableInfo(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(text, pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDtoIn addComment(long userId, long itemId, CommentDtoIn commentDtoIn) {
        validationComment(commentDtoIn);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не удалось найти предмет с id = " + itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id = " + userId));
        //commentDtoIn.setAuthorId(userId);
        commentDtoIn.setAuthorName(user.getName());
        //commentDtoIn.setItemId(itemId);
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(itemId, userId, BookingStatus.APPROVED);
        if ((booking != null) && (booking.getEnd().isBefore/*(commentDtoIn.getCreated()))){*/(LocalDateTime.parse(commentDtoIn.getCreated())))) {
            return CommentMapper.toCommentDtoIn(commentRepository.save(CommentMapper.toComment(commentDtoIn, user, item)));
        }
        throw new ValidationException("Комментарий можно оставить только после окончания бронирования");
    }


    private void validationComment(CommentDtoIn commentDtoIn) {
        String message = "Ошибка валидации вещи: ";
        if (commentDtoIn == null) {
            message += "переданно пустое тело.";
            throw new ValidationException(message);
        }
        if (commentDtoIn.getText() == null) {
            message += "Нельзя создавать пустые комментарии";
        } else {
            return;
        }
        throw new ValidationException(message);
    }

    private void validationItem(ItemDtoIn itemDto) {
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
