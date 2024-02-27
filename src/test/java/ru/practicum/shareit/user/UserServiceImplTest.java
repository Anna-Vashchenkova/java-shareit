package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository mockUserRepository;
    @Captor
    ArgumentCaptor<User> userCaptor;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");

    @Test
    @DisplayName("Показать список всех пользователей")
    public void getAllUsers() {
        List<User> users = mockUserRepository.findAll();
        users.add(validUser1);
        users.add(validUser2);
        Mockito.when(mockUserRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.getAllUsers();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Корректное сохранение пользователя")
    public void saveUser() {
        User user =new User(3l, "new@mail.ru", "new");
        Mockito.when(mockUserRepository.save(any())).thenReturn(user);

        User result = userService.saveUser(3L, "new@mail.ru", "new");

        Assertions.assertEquals(result, user);
    }

    @Test
    @DisplayName("Обновление пользователя")
    public void updateUser() {
        Mockito.when(mockUserRepository.findById(anyLong())).thenReturn(Optional.ofNullable(validUser1));
        validUser1.setName("aaa");
        validUser1.setEmail("aaa@mail.ru");
        userService.updateUser(1L, validUser1);

        verify(mockUserRepository).save(userCaptor.capture());
        Assertions.assertEquals(validUser1, userCaptor.getValue());
    }
}