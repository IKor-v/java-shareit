package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1L, "Link", "spuderman@man.com");
    private final Item item = new Item(1L, "Ocarina", "This is time thing", true, user, null);
    private final BookingDtoOut bookingDtoOut = new BookingDtoOut(1L, now.plusMinutes(5).toString(), now.plusHours(1).toString(),
            ItemMapper.toItemDto(item), UserMapper.toUserDto(user), BookingStatus.WAITING);
    private final Booking booking = new Booking(1L, now.plusMinutes(5), now.plusHours(1), item, user, BookingStatus.WAITING);
    private final BookingDtoIn bookingDtoIn = new BookingDtoIn(1L, now.plusMinutes(5).toString(), now.plusHours(1).toString());
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDtoOut result = bookingService.addBooking(bookingDtoIn, user.getId() + 1);

        assertEquals(bookingDtoOut, result);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void bookingApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDtoOut result = bookingService.bookingApproved(user.getId(), booking.getId(), true);
        bookingDtoOut.setStatus(BookingStatus.APPROVED);

        assertEquals(bookingDtoOut, result);
        bookingDtoOut.setStatus(BookingStatus.WAITING);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.findByItemIdAndStatusInOrderByStartDesc(anyLong(), anyCollection())).thenReturn(List.of());
        BookingDtoOut result = bookingService.getBookingById(user.getId(), booking.getId());

        assertEquals(bookingDtoOut, result);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemIdAndStatusInOrderByStartDesc(anyLong(), anyCollection());
    }

    @Test
    void getAllBookingByBookerIfStateAll() {
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByBooker(user.getId(), "ALL", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(anyLong(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByBookerIfStateCURRENT() {
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByBooker(user.getId(), "CURRENT", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByBookerIfStatePAST() {
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByBooker(user.getId(), "PAST", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByBookerIfStateFUTURE() {
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByBooker(user.getId(), "FUTURE", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByBookerIfStateWAITING() {
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByBooker(user.getId(), "WAITING", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByBookerIfStateREJECTED() {
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByBooker(user.getId(), "REJECTED", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }


    @Test
    void getAllBookingByOwnerIfStateAll() {
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByOwner(user.getId(), "ALL", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdOrderByStartDesc(anyLong(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByOwnerIfStateCURRENT() {
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByOwner(user.getId(), "CURRENT", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByOwnerIfStatePAST() {
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByOwner(user.getId(), "PAST", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByOwnerIfStateFUTURE() {
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByOwner(user.getId(), "FUTURE", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByOwnerIfStateWAITING() {
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByOwner(user.getId(), "WAITING", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }

    @Test
    void getAllBookingByOwnerIfStateREJECTED() {
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByStatusInOrderByStartDesc(any())).thenReturn(List.of());
        List<BookingDtoOut> result = bookingService.getAllBookingByOwner(user.getId(), "REJECTED", 0, 10);

        assertEquals(List.of(bookingDtoOut), result);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatusIsOrderByStartDesc(anyLong(), any(), any());
        verify(bookingRepository, times(1)).findByStatusInOrderByStartDesc(any());
    }


    @Test
    void addBookingWhenBookingIsUser() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDtoIn, user.getId()));

        assertEquals("Нельзя бронировать свои вещи.", exception.getMessage());
        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }
}