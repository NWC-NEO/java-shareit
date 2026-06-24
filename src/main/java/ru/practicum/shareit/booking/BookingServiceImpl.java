package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingInputDto dto) {
        if (dto.getStart() == null || dto.getEnd() == null
                || dto.getStart().isBefore(LocalDateTime.now())
                || dto.getEnd().isBefore(dto.getStart())
                || dto.getStart().equals(dto.getEnd())) {
            throw new ValidationException("Некорректные даты бронирования");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свою же вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        Booking booking = BookingMapper.toBooking(dto, item, user);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Изменить статус может только владелец вещи");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Статус бронирования уже изменен");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нет прав для просмотра этого бронирования");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String stateParam) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        State state = StateParser.parse(stateParam);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case CURRENT ->
                    bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> bookingRepository.findByBooker_IdOrderByStartDesc(userId);
        };
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String stateParam) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        State state = StateParser.parse(stateParam);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case CURRENT ->
                    bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId);
        };
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

}
