package com.example.demo.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class StudentController {

    @GetMapping("/api/v1/student")
    public List<Student> get() {
        return List.of(new Student(1L, "Alina", "alina@bk.ru", LocalDate.now(), 0));
    }

}
