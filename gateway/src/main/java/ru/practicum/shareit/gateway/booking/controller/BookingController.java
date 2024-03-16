package ru.practicum.shareit.gateway.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.booking.controller.dto.BookingIncomeDto;
import ru.practicum.shareit.gateway.booking.controller.dto.BookingOutcomeDto;
import ru.practicum.shareit.gateway.booking.controller.dto.SearchStatus;
import ru.practicum.shareit.gateway.exception.DataNotFoundException;
import ru.practicum.shareit.gateway.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final WebClient webClient;
    private static final String API_PREFIX = "/bookings";

    @PostMapping()
    public Mono<BookingOutcomeDto> saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingIncomeDto dto) {
        log.info("Получен запрос на добавление бронирования '{}' пользователем '{}'",dto, userId);
        return webClient.post()
                .uri(API_PREFIX)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.error(new ValidationException("Невалидные данные запроса")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(BookingOutcomeDto.class);
    }

    @PatchMapping("/{bookingId}")
    public Mono<BookingOutcomeDto> approveBooking(@PathVariable("bookingId") long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление статуса бронирования с ID={}", bookingId);
        if (approved == null) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("bookings", "{bookingId}")
                        .queryParam("approved", approved)
                        .build(bookingId))
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.error(new ValidationException("Невалидные данные запроса")))
                .bodyToMono(BookingOutcomeDto.class);
    }

    @GetMapping("/{bookingId}")
    public Mono<BookingOutcomeDto> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("bookingId") long bookingId) {
        log.info("Получен запрос на получение информации о бронировании с ID={}", bookingId);
        return webClient.get()
                .uri(API_PREFIX + "/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(BookingOutcomeDto.class);
    }

    @GetMapping()
    public List<BookingOutcomeDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam (name = "state", defaultValue = "ALL") String stateParam,
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос на получение " +
                "{} бронирований на странице {} пользователя с ID={} с параметром STATE={}", size, from, userId, stateParam);
        SearchStatus state;
        try {
            state = SearchStatus.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(API_PREFIX)
                        .queryParam("state", stateParam)
                        .queryParam("from", from)
                        .queryParam("size", size).build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(new ParameterizedTypeReference<List<BookingOutcomeDto>>() {
                }).block();
    }

    @GetMapping("/owner")
    public Mono<List<BookingOutcomeDto>> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam (name = "state", defaultValue = "ALL") String stateParam,
                                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос на получение " +
                "{} бронирований на странице {} владельцем вещи с ID={} с параметром STATE={}",size, from, userId, stateParam);
        SearchStatus state;
        try {
            state = SearchStatus.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(API_PREFIX + "/owner")
                        .queryParam("state", stateParam)
                        .queryParam("from", from)
                        .queryParam("size", size).build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(new ParameterizedTypeReference<List<BookingOutcomeDto>>() {
                });
    }
}