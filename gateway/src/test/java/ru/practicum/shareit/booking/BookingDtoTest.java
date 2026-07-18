package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 7, 15, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 15, 12, 0, 0);

        BookingDto.ItemShortDto item = BookingDto.ItemShortDto.builder()
                .id(1L)
                .name("Item")
                .build();

        BookingDto.UserShortDto booker = BookingDto.UserShortDto.builder()
                .id(2L)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(10L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.start").contains("2026-07-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").contains("2026-07-15T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
