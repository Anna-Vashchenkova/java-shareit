package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemOutcomeInfoDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос - показать список вещей пользователя '{}'", userId);
        return itemService.getItems(userId).stream()

                .map(item -> {
                            List<Booking> bookings = bookingService.getBookingsForUser(item.getId());
                            List<Comment> comments = commentService.getComments(item.getId());
                            List<ItemOutcomeInfoDto.CommentDto> commentsDto = comments.stream()
                                    .map(CommentMapper::toCommentDto)
                                    .collect(Collectors.toList());
                            return ItemMapper.toItemInfoDto(item, bookings, commentsDto);
                        })
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemOutcomeDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody ItemIncomeDto dto) {
        log.info("Получен запрос на добавление итема '{}' пользователю '{}'", dto, userId);
        return ItemMapper.toItemDto(itemService.addNewItem(
                userId,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                dto.getRequestId()
        ));
    }

    @PatchMapping("/{itemId}")
    public ItemOutcomeDto updateItem(@PathVariable("itemId") long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody ItemIncomeDto dto) {
        log.info("Получен запрос на обновление данных итема '{}' у пользователя '{}'",itemId, userId);
        return ItemMapper.toItemDto(itemService.updateItem(
                userId,
                itemId,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable()
        ));
    }

    @GetMapping("/{itemId}")
    public ItemOutcomeInfoDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос от пользователя '{}' - показать итем '{}'", userId, itemId);
        Item item = itemService.getItemById(userId, itemId);
        List<ItemOutcomeInfoDto.CommentDto> commentsDto = commentService.getComments(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());;
        if (itemService.userIsOwnerOfItem(userId, itemId)) {
            List<Booking> bookings = bookingService.getBookingsForUser(item.getId());

            ItemOutcomeInfoDto itemDto = ItemMapper.toItemInfoDto(item, bookings, commentsDto);
            return itemDto;
        } else {
            return ItemMapper.toItemDto2(item, commentsDto);
        }

    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на удаление итема '{}' пользователя '{}'",itemId, userId);
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemOutcomeDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam String text) {
        log.info("Получен запрос на поиск итема по содержанию текста '{}' у пользователя '{}'",text, userId);
        return itemService.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public ItemOutcomeInfoDto.CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                                                    @Valid @RequestBody ItemOutcomeInfoDto.CommentDto dto) {
        return CommentMapper.toCommentDto(commentService.addComment(userId, itemId, dto.getText()));
    }
}
