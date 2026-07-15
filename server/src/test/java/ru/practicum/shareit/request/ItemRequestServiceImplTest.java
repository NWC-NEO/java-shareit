package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user;
    private ItemRequest request;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");

        request = ItemRequest.builder()
                .id(1L)
                .description("Need tool")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        requestDto = ItemRequestDto.builder()
                .description("Need tool")
                .build();
    }

    @Test
    void create_whenUserExists_savesRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = requestService.create(1L, requestDto);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
    }

    @Test
    void create_whenUserDoesNotExist_throwsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.create(1L, requestDto));
    }

    @Test
    void getUserRequests_whenUserExists_returnsList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = requestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserRequests_whenUserDoesNotExist_throwsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(1L));
    }

    @Test
    void getAllRequests_returnsOtherUsersRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = requestService.getAllRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRequestById_whenRequestExists_returnsRequest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(10L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = requestService.getRequestById(1L, 10L);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
    }

    @Test
    void getRequestById_whenRequestDoesNotExist_throwsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 10L));
    }
}
