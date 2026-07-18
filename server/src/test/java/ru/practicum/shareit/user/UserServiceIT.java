package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIT {

    private final UserService userService;

    @Test
    void createAndGetUser() {
        UserDto userDto = new UserDto();
        userDto.setName("TestUser");
        userDto.setEmail("test@mail.com");

        UserDto savedUser = userService.createUser(userDto);
        UserDto retrievedUser = userService.getUserById(savedUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(retrievedUser.getName()).isEqualTo(userDto.getName());
        assertThat(retrievedUser.getEmail()).isEqualTo(userDto.getEmail());
    }
}
