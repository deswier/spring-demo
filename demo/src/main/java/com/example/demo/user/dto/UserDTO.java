package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDTO {

    @NotNull
    private final String firstName;

    @NotNull
    private final String lastName;

    @Email
    @NotNull
    private final String email;

    @NotNull
    private final String password;

}
