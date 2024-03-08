package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        Comment com = new Comment();
        com.setAuthor(user);
        com.setItem(item);
        com.setText(commentDto.getText());
        com.setCreated(LocalDateTime.parse(commentDto.getCreated()));
        return com;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getItem().getId(),
                comment.getText(),
                comment.getCreated().toString());
    }
}
