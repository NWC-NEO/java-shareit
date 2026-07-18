package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@mail.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        userDto.setEmail("user@mail.com");
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getEmail(), result.get(0).getEmail());
    }

    @Test
    void getUserById_whenUserExists_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void getUserById_whenUserDoesNotExist_throwsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void createUser_whenEmailUnique_savesUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void createUser_whenEmailExists_throwsConflictException() {
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
    }

    @Test
    void updateUser_whenUserExists_updatesFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        UserDto updateDto = new UserDto();
        updateDto.setName("UpdatedName");
        updateDto.setEmail("updated@mail.com");

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("UpdatedName", result.getName());
        assertEquals("updated@mail.com", result.getEmail());
    }

    @Test
    void updateUser_whenEmailConflict_throwsConflictException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("conflict@mail.com");

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void deleteUser_whenUserExists_deletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_throwsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }
}
