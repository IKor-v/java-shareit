package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.DatabaseItemRepository;
import ru.practicum.shareit.item.ItemServiceImpDb;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.DatabaseUserRepository;
import ru.practicum.shareit.user.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final DatabaseUserRepository userRepository;
    private final DatabaseItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, DatabaseUserRepository userRepository, DatabaseItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }


    @Override
    public BookingDtoOut addBooking(BookingDto bookingDto, Long userId) {
        validationBooking(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Не найден предмет с id = " + bookingDto.getItemId()));
        if (!item.isAvailable()) {
            throw new RuntimeException("Этот предмет недоступен для бронирования.");
        } else if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Нельзя бронировать свои вещи.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut bookingApproved(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
        if (userId.equals(booking.getItem().getOwner().getId())) {
            if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                throw new RuntimeException("Вы уже подтвердили бронирование");
            }
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new NotFoundException("Подтвердить бронирование может только владелец");
        }

        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
        BookingDtoOut result = null;
        if ((userId.equals(booking.getBooker().getId())) || (userId.equals(booking.getItem().getOwner().getId()))) {
            result = BookingMapper.toBookingDtoOut(booking);
        } else {
            throw new NotFoundException("Просматривать подробную информацию о бронировании могут только его участники");
        }
        result.setItem(ItemServiceImpDb.getLastAndNextBooking(result.getItem(), bookingRepository));
        return result;
    }

    @Override
    public Collection<BookingDtoOut> getAllBookingByUser(Long userId, String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (RuntimeException e) {
            throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
        }
        Collection<Booking> result = null;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        switch (bookingState) {
            case ALL: {
                result = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            case PAST: {
                result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case FUTURE: {
                result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case WAITING:
            case REJECTED: {
                result = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.valueOf(bookingState.toString()));
                break;
            }
        }

        return result.stream()
                .map(BookingMapper::toBookingDtoOut)
                .map(bookingDtoOut -> {
                    ItemDto itemDto = ItemServiceImpDb.getLastAndNextBooking(bookingDtoOut.getItem(), bookingRepository);
                    bookingDtoOut.setItem(itemDto);
                    return bookingDtoOut;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Collection<BookingDtoOut> getAllBookingByOwner(Long userId, String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (RuntimeException e) {
            throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
        }
        Collection<Booking> result = null;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
        switch (bookingState) {
            case ALL: {
                result = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            case PAST: {
                result = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case FUTURE: {
                result = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case WAITING:
            case REJECTED: {
                result = bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.valueOf(bookingState.toString()));
                break;
            }

        }


        return result.stream()
                .map(BookingMapper::toBookingDtoOut)
                .map(bookingDtoOut -> {
                    ItemDto itemDto = ItemServiceImpDb.getLastAndNextBooking(bookingDtoOut.getItem(), bookingRepository);
                    bookingDtoOut.setItem(itemDto);
                    return bookingDtoOut;
                })
                .collect(Collectors.toList());
    }

    private void validationBooking(BookingDto bookingDto) {
        String message = "Ошибка валидации вещи: ";
        if (bookingDto == null) {
            message += "переданно пустое тело.";
            throw new ValidationException(message);
        } else if ((bookingDto.getEnd() == null) || (bookingDto.getStart() == null) || (bookingDto.getItemId() == null)) {
            message += "передана не вся информация о вещи";
            throw new ValidationException(message);
        }
        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart());
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd());
        if (start.isAfter(end)) {
            message += "Окончание бронирывание не может быть раньше его начала.";
        } else if ((start.isBefore(LocalDateTime.now())) || (end.isBefore(LocalDateTime.now()))) {
            message += "Нельзя бронировать в прошлое";
        } else if (start.equals(end)) {
            message += "Врему начала и конца бронирование не должно совпадать.";
        } else {
            return;
        }
        throw new ValidationException(message);
    }
}
