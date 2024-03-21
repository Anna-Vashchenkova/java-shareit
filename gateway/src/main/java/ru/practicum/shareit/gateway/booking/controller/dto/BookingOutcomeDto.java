package ru.practicum.shareit.gateway.booking.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.item.controller.dto.ItemOutcomeDto;
import ru.practicum.shareit.gateway.user.controller.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOutcomeDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemOutcomeDto item;
    private UserDto booker;
    private String status;
}
