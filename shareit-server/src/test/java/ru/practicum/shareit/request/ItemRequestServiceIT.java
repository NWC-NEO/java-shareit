package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIT {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void createAndGetUserRequests() {
        UserDto userDto = new UserDto();
        userDto.setName("Requestor");
        userDto.setEmail("requestor@mail.com");
        UserDto requestor = userService.createUser(userDto);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need something")
                .build();

        itemRequestService.create(requestor.getId(), requestDto);

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(requestor.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need something");
        assertThat(requests.get(0).getCreated()).isNotNull();
    }
}
