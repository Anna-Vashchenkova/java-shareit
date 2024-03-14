package ru.practicum.shareit.gateway.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") String userId,
                                     @Valid @RequestBody ItemRequestIncomeDto dto) {
        log.info("Получен запрос на добавление запроса '{}' пользователю '{}'", dto, userId);
        Mono<ItemRequestDto> itemRequestDtoMono = webClient.post()
                .uri("/requests")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ItemRequestDto.class);
        return itemRequestDtoMono.block();
    }

    @GetMapping
    public List<ItemRequestInfoDto> getRequests(@RequestHeader("X-Sharer-User-Id") String userId) {
        log.info("Получен запрос - показать список запросов пользователя '{}'", userId);
        Mono<List<ItemRequestInfoDto>> response = webClient.get()
                .uri("/requests")
                .header("X-Sharer-User-Id", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemRequestInfoDto>>() {
                });
        return response.block();
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос от пользователя '{}'- показать {} запросов других пользователей на {} странице ", userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        Mono<List<ItemRequestInfoDto>> response = webClient.get()
                .uri(uriBuilder ->
                    uriBuilder.path("/requests/all")
                            .queryParam("from", "0")
                            .queryParam("size", "10")
                            .build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemRequestInfoDto>>() {
                });
        return response.block();
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getRequestById(@RequestHeader("X-Sharer-User-Id") String userId,
                                             @PathVariable("requestId") Long requestId) {
        log.info("Получен запрос от пользователя '{}' - показать запрос '{}'", userId, requestId);
        Mono<ItemRequestInfoDto> response = webClient.get()
                .uri("/requests/{requestId}", requestId)
                .header("X-Sharer-User-Id", userId)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.error(new ValidationException("Ошибка валидации")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new DataNotFoundException("Данные не найдены")))
                .bodyToMono(new ParameterizedTypeReference<ItemRequestInfoDto>() {
                });
        return response.block();
    }
}
