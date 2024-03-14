package ru.practicum.shareit.gateway.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        Mono<List<UserDto>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users").build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {});
        return response.block();
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto dto) {
        log.info("Получен запрос на добавление пользователя '{}'", dto);
        Mono<UserDto> userDtoMono = webClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserDto.class);
        return userDtoMono.block();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId, @RequestBody UserDto dto) {
        log.info("Получен запрос на обновление данных пользователя '{}'", userId);
        if (dto.getId() == null) {
            dto.setId(userId);
        }
        Mono<UserDto> userDtoMono = webClient.patch()
                .uri("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserDto.class);
        return userDtoMono.block();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос - показать данные пользователя '{}'", userId);
        Mono<UserDto> userDtoMono = webClient.get()
                .uri("/users/{userId}", userId)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Пользователь не найден")))
                .bodyToMono(UserDto.class);
        return userDtoMono.block();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос - удалить данные пользователя '{}'", userId);
        webClient.delete()
                .uri("/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Void.class).block();
    }
}