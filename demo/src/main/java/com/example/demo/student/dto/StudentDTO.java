package com.example.demo.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentDTO {

    private Long id;

    @NotEmpty(message = "{student.name.not.empty}")
    private String name;

    @NotEmpty(message = "{student.email.not.empty}")
    @Email(message = "{student.email.invalid}")
    private String email;

    @NotNull(message = "{student.dob.not.null}")
    @Past(message = "{student.dob.past}")
    private LocalDate dob;

    public StudentDTO() {
    }

    public StudentDTO(Long id, String name, String email, LocalDate dob) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = dob;
    }

    public StudentDTO(String name, String email, LocalDate dob) {
        this.name = name;
        this.email = email;
        this.dob = dob;
    }

}
