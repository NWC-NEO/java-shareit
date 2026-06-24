package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static Booking toBooking(BookingInputDto dto, Item item, User booker) {
        Booking booking = new Booking();

        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        return booking;
    }

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        BookingDto.ItemShortDto itemDto = new BookingDto.ItemShortDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());

        BookingDto.UserShortDto userDto = new BookingDto.UserShortDto();
        userDto.setId(booking.getBooker().getId());

        dto.setItem(itemDto);
        dto.setBooker(userDto);

        return dto;
    }
}
