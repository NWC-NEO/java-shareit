package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status);

    Booking findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime end, Status status);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, Status status);

    boolean existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime end, Status status);
}
