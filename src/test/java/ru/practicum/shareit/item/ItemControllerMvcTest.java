package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerMvcTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;
    @MockBean
    CommentService commentService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser1, created);
    Item item3 = new Item(3L, "дрель", "ddd", Status.AVAILABLE, validUser2, request1);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser1, created);
    Item item4 = new Item(4L, "дрель2", "ddd2", Status.AVAILABLE, validUser2, request2);
    private ItemRequest request3 = new ItemRequest(3L, "запрос3", validUser2, created);
    Item item1 = new Item(1L, "перфоратор", "vvv", Status.AVAILABLE, validUser1, request3);
    private ItemRequest request4 = new ItemRequest(4L, "запрос4", validUser2, created);
    Item item2 = new Item(2L, "перфоратор2", "vvv2", Status.AVAILABLE, validUser1, request4);

    @Test
    @DisplayName("При запросе должен вернуться список ItemOutcomeInfoDto")
    void get_shouldReturnAllItemsByUser() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(this.itemService.getItems(1L, 0, 10)).thenReturn(items);

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("При запросе должен вернуться ItemOutcomeDto")
    void add_shouldReturnItemOutcomeDto() throws Exception {
        Mockito
                .when(this.itemService.addNewItem(1L, "перфоратор", "vvv", true, 3L))
                .thenReturn(item1);

        mvc.perform(
                post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\n" +
                                "  \"name\": \"перфоратор\",\n" +
                                "  \"description\": \"vvv\",\n" +
                                "  \"available\": true,\n" +
                                "  \"requestId\": 3\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("перфоратор"))
                .andExpect(jsonPath("$.description").value("vvv"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.owner.id").value(1))
                .andExpect(jsonPath("$.owner.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.owner.name").value("Aa"))
                .andExpect(jsonPath("$.requestId").value(3));

    }

    @Test
    void updateItem() {
    }

    @Test
    void getItemById() {
    }

    @Test
    void deleteItem() {
    }

    @Test
    void searchItem() {
    }

    @Test
    void addComment() {
    }
}