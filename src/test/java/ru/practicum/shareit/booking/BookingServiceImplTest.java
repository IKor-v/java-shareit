package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
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
    void addBookingTest() {
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
    void addBookingThenAvailableIsFalseTest() {
        Item item1 = item;
        item1.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.addBooking(bookingDtoIn, user.getId() + 1));
        assertEquals("Этот предмет недоступен для бронирования.", exception.getMessage());
    }

    @Test
    void addBookingIsNullTest() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(null, user.getId() + 1));
        assertEquals("Ошибка валидации вещи: переданно пустое тело.", exception.getMessage());
    }

    @Test
    void addBookingThenStartIsNullTest() {
        BookingDtoIn bookingDtoIn1 = bookingDtoIn;
        bookingDtoIn1.setStart(null);
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDtoIn1, user.getId() + 1));
        assertEquals("Ошибка валидации вещи: передана не вся информация о вещи", exception.getMessage());
    }

    @Test
    void addBookingThenStartAfterEndTest() {
        BookingDtoIn bookingDtoIn1 = bookingDtoIn;
        bookingDtoIn1.setStart(now.plusHours(10).toString());
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDtoIn1, user.getId() + 1));
        assertEquals("Ошибка валидации вещи: Окончание бронирывание не может быть раньше его начала.", exception.getMessage());
    }

    @Test
    void addBookingThenStartBeforeNowTest() {
        BookingDtoIn bookingDtoIn1 = bookingDtoIn;
        bookingDtoIn1.setStart(now.minusHours(10).toString());
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDtoIn1, user.getId() + 1));
        assertEquals("Ошибка валидации вещи: Нельзя бронировать в прошлое.", exception.getMessage());
    }

    @Test
    void addBookingThenStartEqualsEndTest() {
        BookingDtoIn bookingDtoIn1 = bookingDtoIn;
        bookingDtoIn1.setStart(bookingDtoIn1.getEnd());
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDtoIn1, user.getId() + 1));
        assertEquals("Ошибка валидации вещи: Врему начала и конца бронирование не должно совпадать.", exception.getMessage());
    }

    @Test
    void bookingApprovedTest() {
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
    void bookingApprovedInRejectedTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDtoOut result = bookingService.bookingApproved(user.getId(), booking.getId(), false);
        bookingDtoOut.setStatus(BookingStatus.REJECTED);

        assertEquals(bookingDtoOut, result);
        bookingDtoOut.setStatus(BookingStatus.WAITING);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void bookingApprovedIfStateApprovedTest() {
        Booking booking1 = booking;
        booking1.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.bookingApproved(user.getId(), booking.getId(), true));
        assertEquals("Вы уже подтвердили бронирование.", exception.getMessage());
    }

    @Test
    void bookingApprovedButNotOwnerTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.bookingApproved(user.getId() + 1, booking.getId(), true));
        assertEquals("Подтвердить бронирование может только владелец", exception.getMessage());
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.findByItemIdAndStatusInOrderByStartDesc(anyLong(), anyCollection())).thenReturn(List.of());
        BookingDtoOut result = bookingService.getBookingById(user.getId(), booking.getId());

        assertEquals(bookingDtoOut, result);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemIdAndStatusInOrderByStartDesc(anyLong(), anyCollection());
    }

    @Test
    void getBookingByIdButNotOwnerTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.getBookingById(user.getId() + 1, booking.getId()));
        assertEquals("Просматривать подробную информацию о бронировании могут только его участники", exception.getMessage());
    }

    @Test
    void getAllBookingByBookerIfStateAllTest() {
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
    void getAllBookingByBookerIfStateUnsupportedTest() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.getAllBookingByBooker(user.getId(), "Not support", 0, 10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getAllBookingByBookerIfStateCURRENTTest() {
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
    void getAllBookingByBookerIfStatePASTTest() {
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
    void getAllBookingByBookerIfStateFUTURETest() {
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
    void getAllBookingByBookerIfStateWAITINGTest() {
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
    void getAllBookingByBookerIfStateREJECTEDTest() {
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
    void getAllBookingByOwnerIfStateAllTest() {
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
    void getAllBookingByOwnerIfStateUnsupportedTest() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.getAllBookingByOwner(user.getId(), "Not support", 0, 10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getAllBookingByOwnerIfStateCURRENTTest() {
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
    void getAllBookingByOwnerIfStatePASTTest() {
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
    void getAllBookingByOwnerIfStateFUTURETest() {
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
    void getAllBookingByOwnerIfStateWAITINGTest() {
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
    void getAllBookingByOwnerIfStateREJECTEDTest() {
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
    void addBookingWhenBookingIsUserTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDtoIn, user.getId()));

        assertEquals("Нельзя бронировать свои вещи.", exception.getMessage());
        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void bookingEqualsTest() {
        Booking booking1 = new Booking();
        booking1.setId(booking.getId());
        assertEquals(booking1, booking1);
        assertEquals(booking1, booking);
        assertEquals(booking1.hashCode(), booking.hashCode());
    }

    @Test
    void bookingMapperTest() {
        assertEquals(bookingDtoOut, BookingMapper.toBookingDtoOut(booking));
        assertEquals(new ItemDto.BookingDto(1L, now.plusMinutes(5), now.plusHours(1), user.getId()), BookingMapper.toBookingDto(booking));
    }
}