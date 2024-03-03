package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
    private Booking booking1 = new Booking(1L,
            LocalDateTime.of(2024, 3, 1, 12, 0, 0),
            LocalDateTime.of(2024, 3, 1, 13, 0, 0),
            item1,
            validUser2,
            ru.practicum.shareit.booking.Status.APPROVED);

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
    void deleteItem() {
    }

    @Test
    void updateItem() {
    }

    @Test
    void getItemById() {
    }

    @Test
    void findItemsByOwnerId() {
    }

    @Test
    void searchItem() {
    }

    @Test
    void userIsOwnerOfItem() {
    }

    @Test
    void findItemsByRequestId() {
    }
}