package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @GetMapping
    public List<ItemOutcomeInfoDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос - показать список вещей пользователя '{}'", userId);
        return itemService.getItems(userId).stream()

                .map(item ->
                        {
                            List<Booking> bookings = bookingService.getBookingsForUser(item.getId());
                            List<Comment> comments = itemService.getComments(item.getId());
                            List<ItemOutcomeInfoDto.CommentDto> commentsDto = comments.stream()
                                    .map(CommentMapper::toCommentDto)
                                    .collect(Collectors.toList());
                            ItemOutcomeInfoDto itemDto = ItemMapper.toItemInfoDto(item, bookings, commentsDto);
                            return itemDto;
                        })
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemOutcomeDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody ItemIncomeDto dto) {
        log.info("Получен запрос на добавление итема '{}' пользователю '{}'",dto, userId);
        return ItemMapper.toItemDto(itemService.addNewItem(
                userId,
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable()
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
    public ItemOutcomeDto getItemById(@PathVariable("itemId") long itemId) {
        log.info("Получен запрос - показать итем '{}'",itemId);
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
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
    public ItemOutcomeDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                                     @Valid @RequestBody ItemIncomeDto dto) {
        return null;
    }
}
