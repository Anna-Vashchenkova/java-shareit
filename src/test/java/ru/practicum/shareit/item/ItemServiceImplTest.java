package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private LocalDateTime created2 = LocalDateTime.of(2024, 3, 1, 14, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser2, created);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser2, created);
    private Item item1 = new Item(1L, "перфоратор", "vvv", Status.AVAILABLE, validUser1, request1);
    private Item item2 = new Item(2L, "перфоратор2", "vvv2", Status.AVAILABLE, validUser1, request2);
    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @Test
    @DisplayName("Показать список всех итемов")
    void getItems_thenReturnItems() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Page mockPage = Mockito.mock(Page.class);
        Mockito.when(mockPage.getContent()).thenReturn(items);
        Mockito.when(repository.findAllByUserIdPage(eq(1L), any())).thenReturn(mockPage);

        List<Item> result = itemService.getItems(1L, 0, 10);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Вернуть список итемов данного пользователя")
    void getAllItems_thenReturnItems() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(repository.findAllByUserId(1L)).thenReturn(items);

        List<Item> result = itemService.getAllItems(1L);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("При вводе валидных параметров вхоернуть итем ")
    void addNewItem_whenParamIsValid_thenReturnItem() {
        Boolean available = true;
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(itemRequestService.getRequestById(1L, 1L)).thenReturn(request1);
        Mockito.when(repository.save(any())).thenReturn(item1);

        Item resultItem = itemService.addNewItem(1L, "перфоратор", "vvv", available, 1L);

        Assertions.assertEquals(item1, resultItem);
    }

    @Test
    @DisplayName("При вводе параметра available = false вернуть итем со статусом UNAVAILABLE")
    void addNewItem_whenAvailableIsFalse_thenReturnItemIsUNAVAILABLE() {
        Boolean available = false;
        Item itemUnavailable = new Item(1L, "перфоратор", "vvv", Status.UNAVAILABLE, validUser1, request1);
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(itemRequestService.getRequestById(1L, 1L)).thenReturn(request1);
        Mockito.when(repository.save(any())).thenReturn(itemUnavailable);

        Item resultItem = itemService.addNewItem(1L, "перфоратор", "vvv", available, 1L);

        Assertions.assertEquals(itemUnavailable, resultItem);
    }

    @Test
    void deleteItem_thenUserRepository() {
        itemService.deleteItem(1L, 1L);

        verify(repository).deleteByUserIdAndItemId(1L, 1L);
    }

    @Test
    @DisplayName("При валидных параметрах вернуть Item")
    void updateItem_whenParamIsValid_thenReturnItem() {
        Long userId = 1L;
        Boolean available = true;
        Mockito.when(repository.getById(1L)).thenReturn(item1);
        Mockito.when(repository.save(any())).thenReturn(item1);

        Item result = itemService.updateItem(userId, 1L, "перфоратор", "vvv", available);

        Assertions.assertEquals(item1, result);
    }

    @Test
    @DisplayName("Item не найден - вернуть DataNotFoundException")
    void updateItem_whenItemNotFound_thenReturnDataNotFoundException() {
        Mockito.when(repository.getById(10L)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.updateItem(1L, 10L, "перфоратор", "vvv", true));

        Assertions.assertEquals("Вещь с таким id не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Id пользователя и id владельца вещи не совпадают - вернуть DataNotFoundException")
    void updateItem_whenUserIdNotEqualsOwnerId_thenReturnDataNotFoundException() {
        Long userId = 10L;
        Mockito.when(repository.getById(1L)).thenReturn(item1);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.updateItem(userId, 1L, "перфоратор", "vvv", true));

        Assertions.assertEquals("Не трогайте чужое!", exception.getMessage());
    }

    @Test
    @DisplayName("При вводе параметра available = false вернуть итем со статусом UNAVAILABLE")
    void updateItem_whenAvailableIsFalse_thenReturnItemIsUNAVAILABLE() {
        Boolean available = false;
        Item itemUnavailable = new Item(1L, "перфоратор", "vvv", Status.UNAVAILABLE, validUser1, request1);
        Mockito.when(repository.getById(1L)).thenReturn(itemUnavailable);

        itemService.updateItem(1L, 1L, "перфоратор", "vvv", available);

        verify(repository).save(itemCaptor.capture());
        Assertions.assertEquals(Status.UNAVAILABLE, itemCaptor.getValue().getAvailable());
    }

    @Test
    @DisplayName("При валидных параметрах вернуть Item")
    void getItemById_whenParamIsValid_thenReturnItem() {
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(item1));

        Item result = itemService.getItemById(1L, 1L);

        Assertions.assertEquals(item1, result);
    }

    @Test
    @DisplayName("При значении пользователя null вернуть DataNotFoundException")
    void getItemById_whenUserNotFound_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(10L)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.getItemById(10L, 1L));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При значении item =  null вернуть DataNotFoundException")
    void getItemById_whenItemNotFound_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(repository.findById(10L)).thenReturn(Optional.empty());

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.getItemById(1L, 10L));

        Assertions.assertEquals("Вещь с таким id не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное получение списка итемов по id владельца")
    void findItemsByOwnerId_whenUserFound_thenReturnItems() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(repository.findByOwnerId(1L)).thenReturn(items);

        List<Item> result = itemService.findItemsByOwnerId(1L);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("При значении пользователя null вернуть DataNotFoundException")
    void findItemsByOwnerId_whenUserNotFound_thenReturnDataNotFoundException() {
        Mockito.when(userService.getUserById(10L)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.findItemsByOwnerId(10L));

        Assertions.assertEquals("Владелец вещи не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное получение списка итемов при вылидных параметрах метода")
    void searchItem_whenParamIsValid_thenReturnItems() {
        String text = "перфоратор";
        int from = 0;
        int size = 10;
        Page mockPage = Mockito.mock(Page.class);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(mockPage.getContent()).thenReturn(items);
        Mockito.when(repository.searchItem(eq(text), any())).thenReturn(mockPage);

        List<Item> result = itemService.searchItem(text, from, size);

        Assertions.assertEquals(2, result.size());

    }

    @Test
    @DisplayName("При поиску по пустой строке вернуть пустой список итомов")
    void searchItem_whenTextIsEmpty_thenReturnEmptyList() {
        String text = "";
        int from = 0;
        int size = 10;

        List<Item> result = itemService.searchItem(text, from, size);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void userIsOwnerOfItem() {
        Mockito.when(userService.getUserById(1L)).thenReturn(validUser1);
        Mockito.when(repository.getReferenceById(1L)).thenReturn(item1);

        boolean result = itemService.userIsOwnerOfItem(1L, 1L);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Вернуть список итемов по id запроса")
    void findItemsByRequestId_shouldReturnItems() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        Mockito.when(repository.findAllByRequestId(anyLong())).thenReturn(items);

        List<Item> itemsByRequestId = itemService.findItemsByRequestId(1L);

        Assertions.assertEquals(1, itemsByRequestId.size());
    }
}