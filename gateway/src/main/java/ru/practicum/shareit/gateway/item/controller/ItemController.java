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

    @GetMapping("/{itemId}")
    public ItemOutcomeInfoDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос от пользователя '{}' - показать итем '{}'", userId, itemId);
        Mono<ItemOutcomeInfoDto> response = webClient.get()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Итем не найден")))
                .bodyToMono(ItemOutcomeInfoDto.class);
        return response.block();
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на удаление итема '{}' пользователя '{}'",itemId, userId);
        webClient.delete()
                .uri("/items/{itemId}", itemId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @GetMapping("/search")
    public List<ItemOutcomeDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam String text,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос на поиск итема по содержанию текста '{}' у пользователя '{}', {} элементов на {} странице",text, userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        Mono<List<ItemOutcomeDto>> response = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/items/search")
                                .queryParam("text", text)
                                .queryParam("from", from)
                                .queryParam("size", size)
                                .build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemOutcomeDto>>() {
                });
        return response.block();
    }

    @PostMapping("/{itemId}/comment")
    public ItemOutcomeInfoDto.CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId,
                                                    @Valid @RequestBody ItemOutcomeInfoDto.CommentDto dto) {
        Mono<ItemOutcomeInfoDto.CommentDto> response = webClient.post()
                .uri("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.error(new ValidationException("Невалидные данные запроса")))
                .bodyToMono(ItemOutcomeInfoDto.CommentDto.class);
        return response.block();
    }
}
