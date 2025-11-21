package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser_ValidUser_ShouldReturnOk() throws Exception {
        UserDto userDto = new UserDto(null, "John Doe", "john@example.com");

        when(userClient.createUser(ArgumentMatchers.any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_InvalidEmail_ShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(null, "John Doe", "invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_BlankEmail_ShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(null, "John Doe", "");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_NullEmail_ShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(null, "John Doe", null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_ExistingUser_ShouldReturnOk() throws Exception {
        when(userClient.getUser(ArgumentMatchers.eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ValidFullUpdate_ShouldReturnOk() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto("Updated Name", "updated@example.com");

        when(userClient.updateUser(ArgumentMatchers.eq(1L), ArgumentMatchers.any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ValidPartialUpdate_ShouldReturnOk() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto("Updated Name", null);

        when(userClient.updateUser(ArgumentMatchers.eq(1L), ArgumentMatchers.any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ValidEmailUpdate_ShouldReturnOk() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, "updated@example.com");

        when(userClient.updateUser(ArgumentMatchers.eq(1L), ArgumentMatchers.any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_EmptyFields_ShouldReturnBadRequest() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto("", "");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_NullFields_ShouldReturnBadRequest() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_InvalidEmail_ShouldReturnBadRequest() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, "invalid-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_EmptyName_ShouldReturnOk() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto("", "valid@example.com");

        when(userClient.updateUser(ArgumentMatchers.eq(1L), ArgumentMatchers.any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ExistingUser_ShouldReturnOk() throws Exception {
        when(userClient.deleteUser(ArgumentMatchers.eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}