package com.example.demo.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
