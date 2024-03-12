package ru.practicum.shareit.gateway.request.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestIncomeDto {
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
}