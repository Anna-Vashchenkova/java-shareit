package ru.practicum.shareit.gateway.request.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.user.controller.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
}