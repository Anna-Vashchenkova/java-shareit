package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static ItemOutcomeInfoDto.CommentDto toCommentDto(Comment comment) {
        return new ItemOutcomeInfoDto.CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
