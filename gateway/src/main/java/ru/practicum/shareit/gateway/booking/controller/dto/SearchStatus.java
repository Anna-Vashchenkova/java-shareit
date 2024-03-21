package ru.practicum.shareit.gateway.booking.controller.dto;

public enum SearchStatus {
    ALL, //все
    CURRENT, //текущие
    PAST, //завершённые
    FUTURE, //будущие
    WAITING, //ожидающие подтверждения
    REJECTED //отклонённые
}