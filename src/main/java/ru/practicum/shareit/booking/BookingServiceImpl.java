package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }


    @Override
    @Transactional
    public BookingDtoOut addBooking(BookingDtoIn bookingDto, Long userId) {
        validationBooking(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Не найден предмет с id = " + bookingDto.getItemId()));
        if (!item.isAvailable()) {
            throw new RuntimeException("Этот предмет недоступен для бронирования.");
        } else if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Нельзя бронировать свои вещи.");
        }
        Booking booking = BookingMapper.toBookingFromBookingDtoIn(bookingDto,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId)), item);
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut bookingApproved(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
        if (userId.equals(booking.getItem().getOwner().getId())) {
            if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                throw new RuntimeException("Вы уже подтвердили бронирование.");
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
        BookingDtoOut result;
        if ((userId.equals(booking.getBooker().getId())) || (userId.equals(booking.getItem().getOwner().getId()))) {
            result = BookingMapper.toBookingDtoOut(booking);
        } else {
            throw new NotFoundException("Просматривать подробную информацию о бронировании могут только его участники");
        }
        result.setItem(ItemServiceImp.getLastAndNextBooking(result.getItem(),
                bookingRepository.findByItemIdAndStatusInOrderByStartDesc(result.getId(), Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED)), true));
        return result;
    }

    @Override
    public List<BookingDtoOut> getAllBookingByBooker(Long userId, String state, Integer from, Integer size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (RuntimeException e) {
            throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<Booking> result = null;
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));

        ItemRequestServiceImpl.checkPageableInfo(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        switch (bookingState) {
            case ALL: {
                result = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            }
            case PAST: {
                result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case WAITING:
            case REJECTED: {
                result = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.valueOf(bookingState.toString()), pageable);
                break;
            }
        }

        Collection<Booking> bookings = bookingRepository.findByStatusInOrderByStartDesc(List.of(BookingStatus.WAITING, BookingStatus.APPROVED));  //Arrays.asList

        return result.stream()
                .map(BookingMapper::toBookingDtoOut)
                .peek(bookingDtoOut -> {
                    ItemDto itemDto = ItemServiceImp.getLastAndNextBooking(bookingDtoOut.getItem(), bookings, false); //bookingRepository);
                    bookingDtoOut.setItem(itemDto);
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDtoOut> getAllBookingByOwner(Long userId, String state, Integer from, Integer size) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (RuntimeException e) {
            throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<Booking> result = null;
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));

        ItemRequestServiceImpl.checkPageableInfo(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        switch (bookingState) {
            case ALL: {
                result = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            }
            case PAST: {
                result = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case WAITING:
            case REJECTED: {
                result = bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.valueOf(bookingState.toString()), pageable);
                break;
            }
        }
        Collection<Booking> bookings = bookingRepository.findByStatusInOrderByStartDesc(Arrays.asList(BookingStatus.WAITING, BookingStatus.APPROVED));
        return result.stream()
                .map(BookingMapper::toBookingDtoOut)
                .peek(bookingDtoOut -> {
                    ItemDto itemDto = ItemServiceImp.getLastAndNextBooking(bookingDtoOut.getItem(), bookings, false);
                    bookingDtoOut.setItem(itemDto);
                })
                .collect(Collectors.toList());
    }

    private void validationBooking(BookingDtoIn bookingDto) {
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
            message += "Нельзя бронировать в прошлое.";
        } else if (start.equals(end)) {
            message += "Врему начала и конца бронирование не должно совпадать.";
        } else {
            return;
        }
        throw new ValidationException(message);
    }
}
