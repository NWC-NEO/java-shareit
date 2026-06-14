package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
    }

    @Test
    void save_and_findById_workingCorrectly() {
        User user = new User();
        user.setEmail("test@mail.com");

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("test@mail.com", found.get().getEmail());
    }

    @Test
    void existsByEmail_whenExists_returnsTrue() {
        User user = new User();
        user.setEmail("test@mail.com");
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("test@mail.com"));
        assertFalse(userRepository.existsByEmail("other@mail.com"));
    }

    @Test
    void deleteById_removesUser() {
        User user = new User();
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }
}
