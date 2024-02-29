package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerMvcTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    ItemRequestService itemRequestService;
    @MockBean
    ItemService itemService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser1, created);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser1, created);
    private ItemRequest request3 = new ItemRequest(3L, "запрос3", validUser2, created);
    private ItemRequest request4 = new ItemRequest(4L, "запрос4", validUser2, created);


    @Test
    @DisplayName("При запросе должен вернуться ItemRequestDto")
    void addRequest_shouldReturnItemRequestDto() throws Exception {
        Mockito.when(this.itemRequestService.addNewRequest(any(), any())).thenReturn(request1);

        mvc.perform(
                        post("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                                .content("{\n" +
                                        "  \"description\": \"Хотел бы воспользоваться щёткой для обуви\"\n" +
                                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("запрос1"))
                .andExpect(jsonPath("$.requestor.id").value(1))
                .andExpect(jsonPath("$.requestor.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.requestor.name").value("Aa"))
                .andExpect(jsonPath("$.created").value("2024-02-29T12:00:00"));
    }

    @Test
    @DisplayName("При запросе должны вернуться все запросы пользователя")
    void getRequests_shouldReturnAllRequestsByUser() throws Exception {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        Mockito.when(this.itemRequestService.getRequests(1L)).thenReturn(requests);

        mvc.perform(
                        get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("При запросе должны вернуться все запросы других пользователей")
    void getAllRequests_thenReturnAllRequestsByOtherUsers() throws Exception {
        List<ItemRequest> requests2 = new ArrayList<>();
        requests2.add(request3);
        requests2.add(request4);
        Mockito.when(this.itemRequestService.getAllRequests(1L, 0, 10)).thenReturn(requests2);

        mvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 1L)
                                .param("from", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("При запросе должен вернуться ItemRequestInfoDto")
    void getRequestById_shouldReturnItemRequestInfoDto() throws Exception {
        Item item1 = new Item(1L, "item1", "--", Status.AVAILABLE, validUser2, request1);
        Item item2 = new Item(2L, "item2", "--", Status.AVAILABLE, validUser2, request1);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Mockito.when(this.itemService.findItemsByRequestId(1L)).thenReturn(items);
        Mockito.when(this.itemRequestService.getRequestById(1L, 1L)).thenReturn(request1);

        mvc.perform(
                get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("запрос1"))
                .andExpect(jsonPath("$.created").value("2024-02-29T12:00:00"))
                .andExpect(jsonPath("$.items.[0].id").value(1))
                .andExpect(jsonPath("$.items.[0].name").value("item1"))
                .andExpect(jsonPath("$.items.[0].description").value("--"))
                .andExpect(jsonPath("$.items.[0].available").value("true"))
                .andExpect(jsonPath("$.items.[0].owner.id").value(2))
                .andExpect(jsonPath("$.items.[0].owner.email").value("bb@mail.ru"))
                .andExpect(jsonPath("$.items.[0].owner.name").value("Bb"))
                .andExpect(jsonPath("$.items.[0].requestId").value(1))
                .andExpect(jsonPath("$.items.[1].id").value(2L))
                .andExpect(jsonPath("$.items.[1].name").value("item2"))
                .andExpect(jsonPath("$.items.[1].description").value("--"))
                .andExpect(jsonPath("$.items.[1].available").value("true"))
                .andExpect(jsonPath("$.items.[1].owner.id").value(2))
                .andExpect(jsonPath("$.items.[1].owner.email").value("bb@mail.ru"))
                .andExpect(jsonPath("$.items.[1].owner.name").value("Bb"))
                .andExpect(jsonPath("$.items.[1].requestId").value(1));
    }
}