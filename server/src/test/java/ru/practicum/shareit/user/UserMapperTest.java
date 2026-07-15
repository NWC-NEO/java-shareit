package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto() {
        assertNull(UserMapper.toUserDto(null));

        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("email@mail.com");

        UserDto dto = UserMapper.toUserDto(user);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Name", dto.getName());
        assertEquals("email@mail.com", dto.getEmail());
    }

    @Test
    void toUser() {
        assertNull(UserMapper.toUser(null));

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Name");
        dto.setEmail("email@mail.com");

        User user = UserMapper.toUser(dto);
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Name", user.getName());
        assertEquals("email@mail.com", user.getEmail());
    }
}
