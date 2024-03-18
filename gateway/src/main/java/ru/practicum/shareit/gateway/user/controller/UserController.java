package ru.practicum.shareit.gateway.user.controller;

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
import ru.practicum.shareit.gateway.user.controller.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final WebClient webClient;
    private static final String API_PREFIX = "/users";
    private static final String API_PATH = "/{userId}";

    @GetMapping
    public Mono<List<UserDto>> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(API_PREFIX).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {});
    }

    @PostMapping
    public Mono<UserDto> saveNewUser(@Valid @RequestBody UserDto dto) {
        log.info("Получен запрос на добавление пользователя '{}'", dto);
        return webClient.post()
                .uri(API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    @PatchMapping("/{userId}")
    public Mono<UserDto> updateUser(@PathVariable("userId") Long userId, @RequestBody UserDto dto) {
        log.info("Получен запрос на обновление данных пользователя '{}'", userId);
        if (dto.getId() == null) {
            dto.setId(userId);
        }
        return webClient.patch()
                .uri(API_PREFIX + API_PATH, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    @GetMapping("/{userId}")
    public Mono<UserDto> getUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос - показать данные пользователя '{}'", userId);
        return webClient.get()
                .uri(API_PREFIX + API_PATH, userId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new DataNotFoundException("Пользователь не найден")))
                .bodyToMono(UserDto.class);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос - удалить данные пользователя '{}'", userId);
        webClient.delete()
                .uri(API_PREFIX + API_PATH, userId)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}