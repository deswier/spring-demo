package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private final String firstName;

    @NotEmpty
    private final String lastName;

    @NotEmpty(message = "{student.email.not.empty}")
    @Email(message = "{student.email.invalid}")
    private final String email;

    @NotEmpty
    private final String password;

}
