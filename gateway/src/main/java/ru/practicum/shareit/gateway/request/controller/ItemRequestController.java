package ru.practicum.shareit.gateway.request.controller;

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
import ru.practicum.shareit.gateway.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.gateway.request.controller.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.gateway.request.controller.dto.ItemRequestInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final WebClient webClient;
    private static final String API_PREFIX = "/requests";
    private static final String API_PATH = "/{requestId}";


    @PostMapping
    public Mono<ItemRequestDto> addRequest(@RequestHeader("X-Sharer-User-Id") String userId,
                                     @Valid @RequestBody ItemRequestIncomeDto dto) {
        log.info("Получен запрос на добавление запроса '{}' пользователю '{}'", dto, userId);
        return webClient.post()
                .uri(API_PREFIX)
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ItemRequestDto.class);
    }

    @GetMapping
    public Mono<List<ItemRequestInfoDto>> getRequests(@RequestHeader("X-Sharer-User-Id") String userId) {
        log.info("Получен запрос - показать список запросов пользователя '{}'", userId);
        return webClient.get()
                .uri(API_PREFIX)
                .header("X-Sharer-User-Id", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/all")
    public Mono<List<ItemRequestInfoDto>> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос от пользователя '{}'- показать {} запросов других пользователей на {} странице ", userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return webClient.get()
                .uri(uriBuilder ->
                    uriBuilder.path(API_PREFIX + "/all")
                            .queryParam("from", "0")
                            .queryParam("size", "10")
                            .build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/{requestId}")
    public Mono<ItemRequestInfoDto> getRequestById(@RequestHeader("X-Sharer-User-Id") String userId,
                                             @PathVariable("requestId") Long requestId) {
        log.info("Получен запрос от пользователя '{}' - показать запрос '{}'", userId, requestId);
        return webClient.get()
                .uri(API_PREFIX + API_PATH, requestId)
                .header("X-Sharer-User-Id", userId)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.error(new ValidationException("Ошибка валидации")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new DataNotFoundException("Данные не найдены")))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}
