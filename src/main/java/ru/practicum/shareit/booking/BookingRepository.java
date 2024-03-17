package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long id, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemIdAndStatusInOrderByStartDesc(long itemId, Collection<BookingStatus> status);

    Booking findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(long itemId, long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByStatusInOrderByStartDesc(List<BookingStatus> statusList);
}
