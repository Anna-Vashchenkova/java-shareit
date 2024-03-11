package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository mockUserRepository;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");

    @Test
    @DisplayName("Показать список всех пользователей")
    public void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(validUser1);
        users.add(validUser2);
        Mockito.when(mockUserRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.getAllUsers();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Корректное сохранение пользователя")
    public void saveUser_whenUserValid_thenSaveUser() {
        User user = new User(3L, "new@mail.ru", "new");
        Mockito.when(mockUserRepository.save(any())).thenReturn(user);

        User result = userService.saveUser(3L, "new@mail.ru", "new");
        verify(mockUserRepository).save(any());
        Assertions.assertEquals(result, user);
    }

    @Test
    @DisplayName("Некорректное сохранение пользователя")
    public void saveUser_whenUserNotValid_thenNotSaveUser() {
        User user = new User(10L, "new@mail.ru", "new");
        Mockito.when(mockUserRepository.save(any())).thenThrow(new DataNotFoundException("Пользователь не найден"));

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> userService.saveUser(10L, "new@mail.ru", "new"));
        verify(mockUserRepository).save(any());
        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление пользователя")
    public void updateUser_whenUserIsValid() {
        Mockito.when(mockUserRepository.findById(anyLong())).thenReturn(Optional.ofNullable(validUser1));
        validUser1.setName("aaa");
        validUser1.setEmail("aaa@mail.ru");
        userService.updateUser(1L, validUser1);

        verify(mockUserRepository).save(userCaptor.capture());
        Assertions.assertEquals(validUser1, userCaptor.getValue());
    }

    @Test
    @DisplayName("Обновление пользователя если входящий параметр null необходимо выбрасывать исключение ")
    public void updateUser_whenUserNull_thenDataNotFoundException() {
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> userService.updateUser(1L, null));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление пользователя если у входящего параметра пользователя Id null" +
            " пользователь будет сохранен с Id")
    public void updateUser_whenUserIdNull_thenUserSaveWithId() {
        User user = new User(null, "new@mail.ru", "new");
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.ofNullable(validUser1));
        User updateUser = userService.updateUser(1L, user);
        verify(mockUserRepository).save(userCaptor.capture());
        Assertions.assertEquals(1L, userCaptor.getValue().getId());
    }

    @Test
    @DisplayName("Обновление пользователя если у входящего параметра пользователя используемый email" +
            " будет выбрасываться исключение")
    public void updateUser_whenUserEmailNotFree_thenRuntimeException() {
        User user = new User(2L, "aa@mail.ru", "new");
        Mockito.when(mockUserRepository.findById(2L)).thenReturn(Optional.ofNullable(validUser2));
        Mockito.when(mockUserRepository.getUserByEmail("aa@mail.ru")).thenReturn(validUser1);

        final RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> userService.updateUser(2L, user));

        Assertions.assertEquals("Пользователь с таким email уже существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Показать пользователя по Id, когда пользователь найден, возвратить этого пользователя")
    void getUserById_whenUserFound_thenReturnedUser() {
        Mockito.when(mockUserRepository.findById(anyLong())).thenReturn(Optional.ofNullable(validUser1));

        User result = userService.getUserById(1L);

        verify(mockUserRepository).findById(1L);
        Assertions.assertEquals(validUser1, result);
    }

    @Test
    @DisplayName("Показать пользователя по Id, когда пользователь не найден, возвратить ошибку")
    void getUserById_whenUserNotFound_thenDataNotFoundException() {
        Mockito.when(mockUserRepository.findById(anyLong())).thenThrow(new DataNotFoundException("Пользователь не найден"));

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> userService.getUserById(0L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(1L);
        verify(mockUserRepository, times(1)).deleteById(1L);
    }
}