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
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoSerialization() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2026, 8, 10, 14, 0, 0))
                .end(LocalDateTime.of(2026, 8, 15, 14, 0, 0))
                .status(Status.APPROVED)
                .booker(new BookingDto.UserShortDto(2L))
                .item(new BookingDto.ItemShortDto(3L, "Дрель"))
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-08-10T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-08-15T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
    }
}
