package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1L, "Link", "spuderman@man.com");
    private final ItemDto itemDto = new ItemDto(1L, "Ocarina", "This is time thing", true,
            UserMapper.toUserDto(user), null, null, null, List.of());
    private final Item item = new Item(1L, "Ocarina", "This is time thing", true, user, null);
    private final Booking booking = new Booking(1L, now.minusHours(1), now.minusSeconds(1), item, user, BookingStatus.APPROVED);
    private final Comment comment = new Comment(1L, "This is text", now, user, item);
    private final ItemDtoIn itemDtoIn = new ItemDtoIn(1L, "Ocarina", "This is time thing", true, null);
    private final CommentDtoIn commentDtoIn = new CommentDtoIn(1L, user.getName(), "This is text", now.toString());
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ItemServiceImp itemService;

    @Test
    void addItemTest() {
        when(itemRepository.save(any())).thenReturn(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ItemDtoIn itemDtoInResult = itemService.addItem(1L, itemDtoIn);

        assertEquals(itemDtoIn, itemDtoInResult);
        verify(itemRepository, times(1)).save(item);
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository, userRepository);
    }

    @Test
    void addItemThenOwnerNotExistTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemDtoIn));

        assertEquals("Не удалось найти хозяина вещи, данные не верны", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateItemTest() {
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ItemDtoIn itemDtoInResult = itemService.updateItem(1L, 1L, itemDtoIn);

        assertEquals(itemDtoIn, itemDtoInResult);
        verify(itemRepository, times(1)).save(item);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository, userRepository);
    }

    @Test
    void getItemForIdTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderById(anyLong())).thenReturn(List.of());

        ItemDto itemDtoResult = itemService.getItemForId(1L, 1L);
        assertEquals(itemDto, itemDtoResult);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(commentRepository, times(1)).findByItemIdOrderById(anyLong());
    }

    @Test
    void getAllMyItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.findByItemOwnerIdOrderById(anyLong())).thenReturn(List.of());
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(List.of(item));

        List<ItemDto> itemDtoResult = itemService.getAllMyItem(1, 0, 10);
        assertEquals(List.of(itemDto), itemDtoResult);
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findByItemOwnerIdOrderById(anyLong());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
        verify(itemRepository, times(1)).findByOwnerId(anyLong(), any());

    }

    @Test
    void searchForTextTest() {
        when(itemRepository.findByAvailableTrueAndDescriptionContainingIgnoreCase(anyString(), any())).thenReturn(List.of(item));

        List<ItemDto> itemDtoResult = itemService.searchForText("Text", 0, 10);
        assertEquals(List.of(itemDto), itemDtoResult);
        verify(itemRepository, times(1)).findByAvailableTrueAndDescriptionContainingIgnoreCase(anyString(), any());
    }

    @Test
    void addCommentTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(anyLong(), anyLong(), any(BookingStatus.class))).thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoIn commentDtoInResult = itemService.addComment(user.getId(), item.getId(), commentDtoIn);
        assertEquals(commentDtoIn, commentDtoInResult);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(anyLong(), anyLong(), any(BookingStatus.class));
        verify(commentRepository, times(1)).save(any(Comment.class));

    }

    @Test
    void addCommentThenBookingNotPastTest() {
        booking.setStart(now.plusHours(2));
        booking.setEnd(now.plusHours(2));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(anyLong(), anyLong(), any(BookingStatus.class))).thenReturn(booking);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDtoIn));
        assertEquals("Комментарий можно оставить только после окончания бронирования", exception.getMessage());

        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(2));
    }

}
