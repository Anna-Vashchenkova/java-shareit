package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerMvcTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");

    @Test
    @DisplayName("При запросе должны вернуться все пользователи")
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(validUser1);
        users.add(validUser2);
        Mockito.when(this.userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("При запросе должен вернуться UserDto")
    void saveNewUser_shouldReturnUserDto() throws Exception {
        Mockito.when(this.userService.saveUser(any(), any(), any()))
                .thenReturn(validUser1);
        mvc.perform(
                post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"update\",\n" +
                        "  \"email\": \"update@user.com\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.name").value("Aa"));
    }

    @Test
    @DisplayName("При запросе несуществующего пользователя должна появиться ошибка ")
    void updateUser_whenUserNotFound_thenReturnDataNotFoundException() throws Exception {
        Mockito.when(this.userService.updateUser(any(), any()))
                .thenThrow(new DataNotFoundException("Пользователь не найден."));
        mvc.perform(
                patch("/users/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"update\",\n" +
                        "  \"email\": \"update@user.com\"\n" +
                        "}"))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("При запросе должен вернуться UserDto")
    void getUser_thenReturnUserDto() throws Exception {
        Mockito.when(this.userService.getUserById(any()))
                .thenReturn(validUser1);
        mvc.perform(
                    get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.name").value("Aa"));
    }

    @Test
    @DisplayName("При запросе ответ 200")
    void deleteUser() throws Exception {
        mvc.perform(
                    delete("/users/1"))
                .andExpect(status().isOk());
    }
}