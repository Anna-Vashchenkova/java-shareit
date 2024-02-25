package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Valid @RequestBody ItemRequestIncomeDto dto) {
        log.info("Получен запрос на добавление запроса '{}' пользователю '{}'", dto, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addNewRequest(userId, dto.getDescription()));
    }

    @GetMapping
    public List<ItemRequestInfoDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос - показать список запросов пользователя '{}'", userId);
        return itemRequestService.getRequests(userId).stream()
                .map(r -> {
                            List<ItemOutcomeDto> itemsDto = itemService.findItemsByRequestId(r.getId()).stream()
                                    .map(ItemMapper :: toItemDto)
                                    .collect(Collectors.toList());
                            return ItemRequestMapper.toItemRequestDto2(r, itemsDto);
                        })
                .collect(Collectors.toList());
    }
}
