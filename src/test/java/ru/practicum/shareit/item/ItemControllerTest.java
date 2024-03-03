package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemIncomeDto;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.item.dto.ItemOutcomeInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @InjectMocks
    ItemController itemController;
    @Mock
    ItemService itemService;
    @Mock
    BookingService bookingService;
    @Mock
    CommentService commentService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private LocalDateTime created2 = LocalDateTime.of(2024, 3, 1, 14, 0, 0);
    private ItemRequest request3 = new ItemRequest(3L, "запрос3", validUser2, created);
    private Item item1 = new Item(1L, "перфоратор", "vvv", Status.AVAILABLE, validUser1, request3);
    private Booking booking1 = new Booking(1L,
            LocalDateTime.of(2024, 3, 1, 12, 0, 0),
            LocalDateTime.of(2024, 3, 1, 13, 0, 0),
            item1,
            validUser2,
            ru.practicum.shareit.booking.Status.APPROVED);

    @Test
    @DisplayName("При значении параметров from < 0, size < 1 вернуть ошибку")
    void get_whenRequestParamIsNotValid_thenReturnValidationException() {
        int from = -1;
        int size = 0;

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemController.get(1L, from, size));

        Assertions.assertEquals("Неверные параметры запроса", exception.getMessage());
    }

    @Test
    @DisplayName("При запросе вызывается сервис и возвращается ItemOutcomeDto")
    void add_thenUseServiceAndShouldReturnItemOutcomeDto() {
        Mockito.when(itemService.addNewItem(1L, "перфоратор", "vvv", true, 3L)).thenReturn(item1);

        UserDto userDto = new UserDto(1L, "aa@mail.ru", "Aa");
        ItemOutcomeDto result = itemController.add(1L, new ItemIncomeDto("перфоратор", "vvv", true, 3L));
        ItemOutcomeDto dto = new ItemOutcomeDto(1L, "перфоратор", "vvv", true, userDto, 3L);
        verify(itemService).addNewItem(1L, "перфоратор", "vvv", true, 3L);
        Assertions.assertEquals(dto, result);
    }

    @Test
    @DisplayName("При запросе вызывается сервис и возвращается ItemOutcomeDto")
    void updateItem_thenUseServiceAndShouldReturnItemOutcomeDto() {
        Mockito.when(itemService.updateItem(1L, 1L, "перфоратор", "vvv", true))
                .thenReturn(item1);

        UserDto userDto = new UserDto(1L, "aa@mail.ru", "Aa");
        ItemOutcomeDto result = itemController.updateItem(1L,1L, new ItemIncomeDto("перфоратор", "vvv", true, 3L));
        ItemOutcomeDto dto = new ItemOutcomeDto(1L, "перфоратор", "vvv", true, userDto, 3L);
        verify(itemService).updateItem(1L, 1L, "перфоратор", "vvv", true);
        Assertions.assertEquals(dto, result);
    }

    @Test
    @DisplayName("При запросе вызывается сервис и возвращвется ItemOutcomeInfoDto")
    void getItemById_thenUseServiceAndReturnItemOutcomeInfoDto() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment(1L, "super", item1, validUser2, created2);
        comments.add(comment1);

        Mockito.when(itemService.getItemById(1L, 1L)).thenReturn(item1);
        Mockito.when(itemService.userIsOwnerOfItem(1L, 1L)).thenReturn(true);
        Mockito.when(bookingService.getBookingsForUser(1L)).thenReturn(bookings);
        Mockito.when(this.commentService.getComments(1L)).thenReturn(comments);

        UserDto userDto = new UserDto(1L, "aa@mail.ru", "Aa");
        UserDto userDto2 = new UserDto(2L, "bb@mail.ru", "Bb");
        ItemOutcomeInfoDto.BookingDto bookingDto = new ItemOutcomeInfoDto.BookingDto(
                1L,
                2L,
                LocalDateTime.of(2024, 3, 1, 12, 0, 0),
                LocalDateTime.of(2024, 3, 1, 13, 0, 0));
        List<ItemOutcomeInfoDto.CommentDto> commentDtos = new ArrayList<>();
        ItemOutcomeInfoDto.CommentDto commentDto = new ItemOutcomeInfoDto.CommentDto(1L, "super", userDto2.getName(), created2);
        commentDtos.add(commentDto);
        ItemOutcomeInfoDto dto = new ItemOutcomeInfoDto(1L, "перфоратор", "vvv", true, userDto, 3L, bookingDto, null, commentDtos);

        ItemOutcomeInfoDto result = itemController.getItemById(1L, 1L);

        verify(itemService).getItemById(1L, 1L);

        Assertions.assertEquals(dto, result);
    }

    @Test
    @DisplayName("Вызывается сервис")
    void deleteItem_thenUseService() {
        itemController.deleteItem(1L, 1L);

        verify(itemService).deleteItem(1L, 1L);
    }

    @Test
    @DisplayName("При значении параметров from < 0, size < 1 вернуть ошибку")
    void searchItem_whenRequestParamIsNotValid_thenReturnValidationException() {
        int from = -1;
        int size = 0;
        String text = "search";

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemController.searchItem(1L, text, from, size));

        Assertions.assertEquals("Неверные параметры запроса", exception.getMessage());
    }

    @Test
    @DisplayName("Вызывается сервис и возвращается CommentDto")
    void addComment_thenUseServiceAndReturnCommentDto() {
        Comment comment1 = new Comment(1L, "super", item1, validUser2, created2);
        Mockito.when(commentService.addComment(1L, 1L, "super")).thenReturn(comment1);

        ItemOutcomeInfoDto.CommentDto dto = new ItemOutcomeInfoDto.CommentDto(1L, "super", "Bb", created2);
        ItemOutcomeInfoDto.CommentDto result = itemController.addComment(1L, 1L, new ItemOutcomeInfoDto.CommentDto(1L, "super", "Bb", created2));
        verify(commentService).addComment(1L, 1L, "super");

        Assertions.assertEquals(dto, result);
    }
}