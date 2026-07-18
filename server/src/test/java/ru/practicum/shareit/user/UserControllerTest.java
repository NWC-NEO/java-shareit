package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        userDto.setEmail("user@mail.com");

        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User"))
                .andExpect(jsonPath("$[0].email").value("user@mail.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        userDto.setEmail("user@mail.com");

        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    void createUser() throws Exception {
        UserDto input = new UserDto();
        input.setName("New");
        input.setEmail("new@mail.com");

        UserDto output = new UserDto();
        output.setId(1L);
        output.setName("New");
        output.setEmail("new@mail.com");

        when(userService.createUser(any(UserDto.class))).thenReturn(output);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void updateUser() throws Exception {
        UserDto input = new UserDto();
        input.setName("Updated");

        UserDto output = new UserDto();
        output.setId(1L);
        output.setName("Updated");
        output.setEmail("old@mail.com");

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(output);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }
}
