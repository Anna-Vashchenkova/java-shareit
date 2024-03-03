package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository repository;
    @Mock
    private UserService userService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser1, created);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser1, created);

    @Test
    @DisplayName("Корректное сохранение запроса")
    void addNewRequest_whenRequestValid_thenSaveRequest() {
        Mockito.when(userService.getUserById(any())).thenReturn(validUser1);
        Mockito.when(repository.save(any())).thenReturn(request1);

        ItemRequest result = itemRequestService.addNewRequest(1L, "запрос1");

        verify(repository).save(any());
        Assertions.assertEquals(request1, result);
    }

    @Test
    @DisplayName("Некорректное сохранение запроса если пользователь null вернуть ошибку")
    void addNewRequest_whenUserIsNull_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(any())).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemRequestService.addNewRequest(10L, "нужна вещь"));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Некорректное сохранение запроса если описание запроса null вернуть ошибку")
    void addNewRequest_whenDescriptionIsNull_ThenReturnValidationException() {
        Mockito.when(userService.getUserById(any())).thenReturn(validUser1);

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemRequestService.addNewRequest(1L, null));

        Assertions.assertEquals("Описание запроса не может быть пустым", exception.getMessage());
    }

    @Test
    @DisplayName("При успешном выполнении запроса вернуть запрос")
    void getRequestById_whenRequestValid_thenReturnRequest() {
        Mockito.when(userService.getUserById(any())).thenReturn(validUser1);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(request1));

        ItemRequest result = itemRequestService.getRequestById(1L, 1L);

        verify(repository).findById(1L);

        Assertions.assertEquals(request1, result);
    }

    @Test
    @DisplayName("При значении пользователя null вернуть ошибку DataNotFoundException")
    void getRequestById_whenUserIsNull_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(any())).thenThrow(new DataNotFoundException("Пользователь не найден."));

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemRequestService.getRequestById(10L, 1L));
        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При значении ItemRequest null вернуть ошибку DataNotFoundException")
    void getRequestById_whenItemRequestIsNull_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 10L));

        Assertions.assertEquals("Запрос с таким id не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При успешном выполнении запроса вернуть список запросов")
    void getRequests_whenItemRequestIsValid_thenReturnListItemRequests() {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(repository.findAllByUserId(1L)).thenReturn(requests);

        List<ItemRequest> result = itemRequestService.getRequests(1L);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("При значении пользователя null вернуть ошибку DataNotFoundException")
    void getRequests_whenUserIsNull_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(any())).thenThrow(new DataNotFoundException("Пользователь не найден."));

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemRequestService.getRequests(10L));
        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При успешном выполнении запроса вернуть список запросов")
    void getAllRequests_whenItemRequestIsValid_thenReturnListItemRequests() {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        Page mockPage = Mockito.mock(Page.class);
        Mockito.when(mockPage.getContent()).thenReturn(requests);
        Mockito.when(userService.getUserById(eq(2L))).thenReturn(validUser2);
        Mockito.when(repository.findAll(eq(2L), any())).thenReturn(mockPage);

        List<ItemRequest> result = itemRequestService.getAllRequests(2L, 0, 10);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("При значении пользователя null вернуть ошибку DataNotFoundException")
    void getAllRequests_whenUserIsNull_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(any())).thenThrow(new DataNotFoundException("Пользователь не найден."));

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemRequestService.getAllRequests(10L, 0, 1));
        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }
}