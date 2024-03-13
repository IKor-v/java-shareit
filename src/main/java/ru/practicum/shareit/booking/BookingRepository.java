package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBookerIdOrderByStartDesc(Long id);

    Collection<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now);

    Collection<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now);

    Collection<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long id, LocalDateTime start, LocalDateTime end);

    Collection<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long id, BookingStatus status);

    Collection<Booking> findByItemOwnerIdOrderByStartDesc(Long id);

    Collection<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    Collection<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    Collection<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    Collection<Booking> findByItemIdAndStatusInOrderByStartDesc(long itemId, Collection<BookingStatus> status);

    Booking findFirstByItemIdAndBookerIdAndStatusIsOrderByStartAsc(long itemId, long userId, BookingStatus status);

    Collection<Booking> findByItemOwnerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status);

    Collection<Booking> findByStatusInOrderByStartDesc(List<BookingStatus> statusList);
}
