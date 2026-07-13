package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIT {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createBooking() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner2@mail.com");
        UserDto owner = userService.createUser(ownerDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("Booker");
        bookerDto.setEmail("booker@mail.com");
        UserDto booker = userService.createUser(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Desc");
        itemDto.setAvailable(true);
        ItemResponseDto item = itemService.createItem(owner.getId(), itemDto);

        BookingInputDto inputDto = new BookingInputDto();
        inputDto.setItemId(item.getId());
        inputDto.setStart(LocalDateTime.now().plusDays(1));
        inputDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto bookingDto = bookingService.create(booker.getId(), inputDto);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(bookingDto.getStatus()).isEqualTo(Status.WAITING);
    }
}
