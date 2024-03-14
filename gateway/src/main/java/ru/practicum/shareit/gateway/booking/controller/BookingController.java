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

    @PostMapping()
    public BookingOutcomeDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingIncomeDto dto) {
        log.info("Получен запрос на добавление бронирования '{}' пользователем '{}'",dto, userId);
        Mono<BookingOutcomeDto> response = webClient.post()
                .uri("/bookings")
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.error(new ValidationException("Невалидные данные запроса")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(BookingOutcomeDto.class);
        return response.block();
    }

    @PatchMapping("/{bookingId}")
    public BookingOutcomeDto approveBooking(@PathVariable("bookingId") long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление статуса бронирования с ID={}", bookingId);
        if (approved == null) {
            throw new ValidationException("Неверные параметры запроса");
        }
        Mono<BookingOutcomeDto> response = webClient.patch()
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
        return response.block();
    }

    @GetMapping("/{bookingId}")
    public BookingOutcomeDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("bookingId") long bookingId) {
        log.info("Получен запрос на получение информации о бронировании с ID={}", bookingId);
        Mono<BookingOutcomeDto> response = webClient.get()
                .uri("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(BookingOutcomeDto.class);
        return response.block();
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
        Mono<List<BookingOutcomeDto>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", stateParam)
                        .queryParam("from", from)
                        .queryParam("size", size).build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(new ParameterizedTypeReference<List<BookingOutcomeDto>>() {
                });
            return response.block();
    }

    @GetMapping("/owner")
    public List<BookingOutcomeDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
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
        Mono<List<BookingOutcomeDto>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", stateParam)
                        .queryParam("from", from)
                        .queryParam("size", size).build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new DataNotFoundException("Бронирование не найдено")))
                .bodyToMono(new ParameterizedTypeReference<List<BookingOutcomeDto>>() {
                });
        return response.block();
    }
}