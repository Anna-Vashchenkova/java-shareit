package ru.practicum.shareit.gateway.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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
    private static final String API_PREFIX = "/items";
    private static final String API_PATH = "/{itemId}";

    @GetMapping
    public Mono<List<ItemOutcomeInfoDto>> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос - показать список вещей пользователя '{}' по {} элементов на странице {}", userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(API_PREFIX)
                        .queryParam("from", from)
                        .queryParam("size", size).build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    @PostMapping
    public Mono<ItemOutcomeDto> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemIncomeDto dto) {
        log.info("Получен запрос на добавление итема '{}' пользователю '{}'", dto, userId);
        return webClient.post()
                .uri(API_PREFIX)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new DataNotFoundException("Итем не найден")))
                .bodyToMono(ItemOutcomeDto.class);
    }

    @PatchMapping("/{itemId}")
    public Mono<ItemOutcomeDto> updateItem(@PathVariable("itemId") long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody ItemIncomeDto dto) {
        log.info("Получен запрос на обновление данных итема '{}' у пользователя '{}'",itemId, userId);
        return webClient.patch()
                .uri(API_PREFIX + API_PATH, itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new DataNotFoundException("Итем не найден")))
                .bodyToMono(ItemOutcomeDto.class);
    }

    @GetMapping("/{itemId}")
    public Mono<ItemOutcomeInfoDto> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос от пользователя '{}' - показать итем '{}'", userId, itemId);
        return webClient.get()
                .uri(API_PREFIX + API_PATH, itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new DataNotFoundException("Итем не найден")))
                .bodyToMono(ItemOutcomeInfoDto.class);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на удаление итема '{}' пользователя '{}'",itemId, userId);
        webClient.delete()
                .uri(API_PREFIX + API_PATH, itemId)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    @GetMapping("/search")
    public Mono<List<ItemOutcomeDto>> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam String text,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос на поиск итема по содержанию текста '{}' у пользователя '{}', {} элементов на {} странице",text, userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(API_PREFIX + "/search")
                                .queryParam("text", text)
                                .queryParam("from", from)
                                .queryParam("size", size)
                                .build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemOutcomeDto>>() {
                });
    }

    @PostMapping("/{itemId}/comment")
    public Mono<ItemOutcomeInfoDto.CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                                                    @Valid @RequestBody ItemOutcomeInfoDto.CommentDto dto) {
        return webClient.post()
                .uri(API_PREFIX + API_PATH + "/comment", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.error(new ValidationException("Невалидные данные запроса")))
                .bodyToMono(ItemOutcomeInfoDto.CommentDto.class);
    }
}
