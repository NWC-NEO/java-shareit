package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {

    @Test
    void toBooking() {
        BookingInputDto dto = new BookingInputDto();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(1L);

        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = BookingMapper.toBooking(dto, item, booker);
        assertNotNull(booking);
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void toDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        User booker = new User();
        booker.setId(2L);

        Booking booking = Booking.builder()
                .id(10L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .item(item)
                .booker(booker)
                .build();

        BookingDto dto = BookingMapper.toDto(booking);
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(Status.APPROVED, dto.getStatus());
        assertEquals(1L, dto.getItem().getId());
        assertEquals("Item", dto.getItem().getName());
        assertEquals(2L, dto.getBooker().getId());
    }
}
