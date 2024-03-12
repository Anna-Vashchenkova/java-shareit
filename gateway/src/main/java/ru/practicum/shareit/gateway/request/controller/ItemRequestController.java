package ru.practicum.shareit.gateway.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final WebClient webClient;
    //private final ItemRequestService itemRequestService;
    //private final ItemService itemService;

    /*@PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Valid @RequestBody ItemRequestIncomeDto dto) {
        log.info("Получен запрос на добавление запроса '{}' пользователю '{}'", dto, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addNewRequest(userId, dto.getDescription()));
    }*/

    /*@GetMapping
    public List<ItemRequestInfoDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос - показать список запросов пользователя '{}'", userId);
        return itemRequestService.getRequests(userId).stream()
                .map(r -> {
                            List<ItemOutcomeDto> itemsDto = itemService.findItemsByRequestId(r.getId()).stream()
                                    .map(ItemMapper::toItemDto)
                                    .collect(Collectors.toList());
                            return ItemRequestMapper.toItemRequestDto2(r, itemsDto);
                        })
                .collect(Collectors.toList());
    }*/

    /*@GetMapping("/all")
    public List<ItemRequestInfoDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос от пользователя '{}'- показать {} запросов других пользователей на {} странице ", userId, size, from);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Неверные параметры запроса");
        }
        return itemRequestService.getAllRequests(userId, from / size, size).stream()
                .map(r -> {
                    List<ItemOutcomeDto> itemsDto = itemService.findItemsByRequestId(r.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto2(r, itemsDto);
                })
                .collect(Collectors.toList());
    }*/

    /*@GetMapping("/{requestId}")
    public ItemRequestInfoDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("requestId") Long requestId) {
        log.info("Получен запрос от пользователя '{}' - показать запрос '{}'", userId, requestId);
        List<ItemOutcomeDto> itemsDto = itemService.findItemsByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto2(itemRequestService.getRequestById(userId, requestId), itemsDto);
    }*/
}
