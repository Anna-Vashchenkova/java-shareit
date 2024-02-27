package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    UserController userController;
    @Mock
    UserService mockUserService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Вызывается сервис")
    void getAllUsers_thenUseService() {
        userController.getAllUsers();
        verify(mockUserService).getAllUsers();
    }

    @Test
    @DisplayName("Возвращается список пользователей дто")
    void getAllUsers_shouldReturnUserDtoList() {
        List<User> users = new ArrayList<>();
        users.add(validUser1);
        users.add(validUser2);
        Mockito.when(mockUserService.getAllUsers()).thenReturn(users);

        List<UserDto> result = userController.getAllUsers();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Вызывается сервис")
    void saveNewUser_thenUseService() {
        User user = new User(3L, "nn@mail.ru", "nnn");
        Mockito.when(mockUserService.saveUser(3L, "nn@mail.ru", "nnn"))
                .thenReturn(user);

        userController.saveNewUser(new UserDto(3L, "nn@mail.ru", "nnn"));
        verify(mockUserService).saveUser(3L, "nn@mail.ru", "nnn");
    }

    @Test
    @DisplayName("Возвращается пользователь дто")
    void saveNewUser_shouldReturnUserDto() {
        User user = new User(3L, "nn@mail.ru", "nnn");
        Mockito.when(mockUserService.saveUser(null, "nn@mail.ru", "nnn"))
                .thenReturn(user);

        UserDto result = userController.saveNewUser(new UserDto(null, "nn@mail.ru", "nnn"));
        UserDto userDto = new UserDto(3L, "nn@mail.ru", "nnn");

        Assertions.assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Обновление пользователя если у пользователя Id null пользователь будет сохранен с Id")
    void updateUser_whenUserIdNull_thenUserReturnWithId() {
        UserDto userDto = new UserDto(null, "nn@mail.ru", "nnn");
        User user = new User(3L, "nn@mail.ru", "nnn");
        Mockito.when(mockUserService.updateUser(anyLong(), any())).thenReturn(user);
        UserDto result = userController.updateUser(3L, userDto);

        Assertions.assertEquals(3L, result.getId());
    }

    @Test
    @DisplayName("Обновление пользователя - вызывается сервис")
    void updateUser_thenUseService() {
        UserDto userDto = new UserDto(null, "nn@mail.ru", "nnn");
        User user = new User(3L, "nn@mail.ru", "nnn");
        Mockito.when(mockUserService.updateUser(anyLong(), any())).thenReturn(user);

        userController.updateUser(3L, userDto);

        verify(mockUserService).updateUser(3L, user);
    }

    @Test
    @DisplayName("Обновление пользователя - возвращается дто пользователь")
    void updateUser_thenReturnUserDto() {
        UserDto userDto = new UserDto(3L, "nn@mail.ru", "nnn");
        User user = new User(3L, "nn@mail.ru", "nnn");
        Mockito.when(mockUserService.updateUser(anyLong(), any())).thenReturn(user);

        UserDto result = userController.updateUser(3L, new UserDto(3L, "nn@mail.ru", "nnn"));

        Assertions.assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Вызывается сервис")
    void getUser_thenUseService() {
        Mockito.when(mockUserService.getUserById(1L)).thenReturn(validUser1);

        userController.getUser(1L);

        verify(mockUserService).getUserById(1L);
    }

    @Test
    @DisplayName("возвращается дто пользователь")
    void getUser_thenReturnUserDto() {
        UserDto userDto = new UserDto(3L, "nn@mail.ru", "nnn");
        User user = new User(3L, "nn@mail.ru", "nnn");
        Mockito.when(mockUserService.getUserById(3L)).thenReturn(user);

        UserDto result = userController.getUser(3L);

        Assertions.assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Вызывается сервис")
    void deleteUser_thenUseService() {
        userController.deleteUser(1L);

        verify(mockUserService).deleteUserById(1L);
    }
}