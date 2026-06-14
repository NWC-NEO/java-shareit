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

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void createUser_whenValid_thenReturns200() throws Exception {
        UserDto input = new UserDto();
        input.setName("Test");
        input.setEmail("test@mail.com");

        UserDto output = new UserDto();
        output.setId(1L);
        output.setName("Test");
        output.setEmail("test@mail.com");

        when(userService.createUser(any())).thenReturn(output);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void createUser_whenEmailInvalid_thenReturns400() throws Exception {
        UserDto input = new UserDto();
        input.setName("Test");
        input.setEmail("invalid");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_whenExists_thenReturns200() throws Exception {
        UserDto output = new UserDto();
        output.setId(1L);
        output.setName("Test");

        when(userService.getUserById(1L)).thenReturn(output);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllUsers_thenReturnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new UserDto()));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateUser_thenReturns200() throws Exception {
        UserDto input = new UserDto();
        input.setName("Updated");

        UserDto output = new UserDto();
        output.setId(1L);
        output.setName("Updated");

        when(userService.updateUser(eq(1L), any())).thenReturn(output);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteUser_thenReturns200() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(1L);
    }
}
