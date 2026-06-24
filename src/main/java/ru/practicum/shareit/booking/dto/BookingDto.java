package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemShortDto item;
    private UserShortDto booker;
    private Status status;

    @Data
    public static class ItemShortDto {
        private Long id;
        private String name;
    }

    @Data
    public static class UserShortDto {
        private Long id;
    }
}
