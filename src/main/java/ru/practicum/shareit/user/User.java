package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
