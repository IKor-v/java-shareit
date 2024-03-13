package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getItem().getId(),
                comment.getText(),
                comment.getCreated().toString());
    }

    public Comment toComment(CommentDtoIn commentDtoIn, User user, Item item) {
        Comment com = new Comment();
        com.setAuthor(user);
        com.setItem(item);
        com.setText(commentDtoIn.getText());
        com.setCreated(LocalDateTime.parse(commentDtoIn.getCreated()));
        return com;
    }

    public CommentDtoIn toCommentDtoIn(Comment comment) {
        return new CommentDtoIn(comment.getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated().toString());
    }
}
