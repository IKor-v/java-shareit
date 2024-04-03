package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdOrderByStartDesc(Long id, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long id, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Long id, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemIdAndStatusInOrderByStartDesc(long itemId, Collection<BookingStatus> status);

    Booking findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(long itemId, long userId, BookingStatus status);

    Page<Booking> findByItemOwnerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByStatusInOrderByStartDesc(List<BookingStatus> statusPage);
}
