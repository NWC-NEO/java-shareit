package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void isCommentAllowed_returnsCorrectBoolean() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@mail.com");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking expiredBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(expiredBooking);

        boolean allowed = bookingRepository.isCommentAllowed(
                booker.getId(),
                item.getId(),
                Status.APPROVED,
                LocalDateTime.now()
        );

        assertTrue(allowed);

        boolean notAllowed = bookingRepository.isCommentAllowed(
                owner.getId(),
                item.getId(),
                Status.APPROVED,
                LocalDateTime.now()
        );

        assertFalse(notAllowed);
    }
}
