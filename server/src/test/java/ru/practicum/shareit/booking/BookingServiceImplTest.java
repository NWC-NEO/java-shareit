package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingInputDto inputDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);

        booker = new User();
        booker.setId(2L);

        item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setAvailable(true);
        item.setName("Item");

        booking = Booking.builder()
                .id(100L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        inputDto = new BookingInputDto();
        inputDto.setItemId(10L);
        inputDto.setStart(LocalDateTime.now().plusDays(1));
        inputDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_withInvalidDates_throwsValidationException() {
        inputDto.setStart(LocalDateTime.now().minusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.create(2L, inputDto));

        inputDto.setStart(LocalDateTime.now().plusDays(5));
        inputDto.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(ValidationException.class, () -> bookingService.create(2L, inputDto));
    }

    @Test
    void create_whenOwnerTriesToBook_throwsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, inputDto));
    }

    @Test
    void create_whenItemNotAvailable_throwsValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(2L, inputDto));
    }

    @Test
    void create_success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.create(2L, inputDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void approve_whenNotOwner_throwsValidationException() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(2L, 100L, true));
    }

    @Test
    void approve_whenStatusNotWaiting_throwsValidationException() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(1L, 100L, true));
    }

    @Test
    void approve_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.approve(1L, 100L, true);

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void getById_whenNotBookerOrOwner_throwsNotFoundException() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(999L, 100L));
    }

    @Test
    void getById_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(2L, 100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
    }

    @Test
    void getUserBookings_allStatesCovered() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));

        when(bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(eq(2L), any())).thenReturn(List.of(booking));
        assertFalse(bookingService.getUserBookings(2L, "PAST").isEmpty());

        when(bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(eq(2L), any())).thenReturn(List.of(booking));
        assertFalse(bookingService.getUserBookings(2L, "FUTURE").isEmpty());

        when(bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(eq(2L), any(), any())).thenReturn(List.of(booking));
        assertFalse(bookingService.getUserBookings(2L, "CURRENT").isEmpty());

        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(2L, Status.WAITING)).thenReturn(List.of(booking));
        assertFalse(bookingService.getUserBookings(2L, "WAITING").isEmpty());

        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(2L, Status.REJECTED)).thenReturn(List.of(booking));
        assertFalse(bookingService.getUserBookings(2L, "REJECTED").isEmpty());

        when(bookingRepository.findByBooker_IdOrderByStartDesc(2L)).thenReturn(List.of(booking));
        assertFalse(bookingService.getUserBookings(2L, "ALL").isEmpty());
    }

    @Test
    void getOwnerBookings_allStatesCovered() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        when(bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(eq(1L), any())).thenReturn(List.of(booking));
        assertFalse(bookingService.getOwnerBookings(1L, "PAST").isEmpty());

        when(bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(eq(1L), any())).thenReturn(List.of(booking));
        assertFalse(bookingService.getOwnerBookings(1L, "FUTURE").isEmpty());

        when(bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L), any(), any())).thenReturn(List.of(booking));
        assertFalse(bookingService.getOwnerBookings(1L, "CURRENT").isEmpty());

        when(bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(1L, Status.WAITING)).thenReturn(List.of(booking));
        assertFalse(bookingService.getOwnerBookings(1L, "WAITING").isEmpty());

        when(bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(1L, Status.REJECTED)).thenReturn(List.of(booking));
        assertFalse(bookingService.getOwnerBookings(1L, "REJECTED").isEmpty());

        when(bookingRepository.findByItem_Owner_IdOrderByStartDesc(1L)).thenReturn(List.of(booking));
        assertFalse(bookingService.getOwnerBookings(1L, "ALL").isEmpty());
    }
}
