package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_whenValid_thenSavesUser() {
        UserDto input = new UserDto();
        input.setName("Test");
        input.setEmail("test@mail.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test");
        savedUser.setEmail("test@mail.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(input);

        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_whenEmailExists_thenThrowsConflictException() {
        UserDto input = new UserDto();
        input.setEmail("test@mail.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(input));
    }

    @Test
    void getUserById_whenNotFound_thenThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_whenValid_thenUpdatesUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old");
        existingUser.setEmail("old@mail.com");

        UserDto input = new UserDto();
        input.setName("New");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.updateUser(1L, input);

        assertEquals("New", result.getName());
        verify(userRepository, times(1)).update(any(User.class));
    }
}
