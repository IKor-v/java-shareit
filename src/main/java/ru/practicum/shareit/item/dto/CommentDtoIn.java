package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentDtoIn {
    private Long id;
    private String authorName;
    @NotBlank
    private String text;
    private String created = LocalDateTime.now().toString();
}
