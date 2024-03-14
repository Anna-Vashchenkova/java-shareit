package ru.practicum.shareit.gateway.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.exception.DataNotFoundException;
import ru.practicum.shareit.gateway.exception.ValidationException;
import ru.practicum.shareit.gateway.item.controller.dto.ItemIncomeDto;
import ru.practicum.shareit.gateway.item.controller.dto.ItemOutcomeDto;
import ru.practicum.shareit.gateway.item.controller.dto.ItemOutcomeInfoDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final WebClient webClient;

    @GetMapping
    public List<ItemOutcomeInfoDto> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос - показать список вещей пользователя '{}' по {} элементов на странице {}", userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        Mono<List<ItemOutcomeInfoDto>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("from", from)
                        .queryParam("size", size).build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemOutcomeInfoDto>>() {
                });
        return response.block();
    }

    @PostMapping
    public ItemOutcomeDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemIncomeDto dto) {
        log.info("Получен запрос на добавление итема '{}' пользователю '{}'", dto, userId);
        Mono<ItemOutcomeDto> response = webClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Итем не найден")))
                .bodyToMono(ItemOutcomeDto.class);
        return response.block();
    }

    @PatchMapping("/{itemId}")
    public ItemOutcomeDto updateItem(@PathVariable("itemId") long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody ItemIncomeDto dto) {
        log.info("Получен запрос на обновление данных итема '{}' у пользователя '{}'",itemId, userId);
        Mono<ItemOutcomeDto> response = webClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Итем не найден")))
                .bodyToMono(ItemOutcomeDto.class);
        return response.block();
    }

    /*@GetMapping("/{itemId}")
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

    }*/

    /*@DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на удаление итема '{}' пользователя '{}'",itemId, userId);
        itemService.deleteItem(userId, itemId);
    }*/

    /*@GetMapping("/search")
    public List<ItemOutcomeDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam String text,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос на поиск итема по содержанию текста '{}' у пользователя '{}', {} элементов на {} странице",text, userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return itemService.searchItem(text, from / size, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }*/

    /*@PostMapping("/{itemId}/comment")
    public ItemOutcomeInfoDto.CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                                                    @Valid @RequestBody ItemOutcomeInfoDto.CommentDto dto) {
        return CommentMapper.toCommentDto(commentService.addComment(userId, itemId, dto.getText()));
    }*/
}
