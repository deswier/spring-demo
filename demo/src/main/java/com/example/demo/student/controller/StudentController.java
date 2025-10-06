package com.example.demo.student.controller;

import com.example.demo.student.dto.StudentDTO;
import com.example.demo.student.exception.StudentValidateException;
import com.example.demo.student.model.Student;
import com.example.demo.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(path = "/api/v1/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    //example: http://localhost:8080/api/v1/student/students?page=0&pageSize=5
    @GetMapping("/students")
    public Page<Student> getStudents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            return Page.empty();
        }

        return studentService.getStudents(page, pageSize);
    }

    @PostMapping
    public ResponseEntity<Void> registerNewStudent(@Valid @RequestBody StudentDTO student) {
        studentService.addNewStudent(student);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // example: http://localhost:8080/api/v1/student/1
    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable("studentId") Long id) throws StudentValidateException {
        studentService.deleteStudent(id);
    }

    // example: http://localhost:8080/api/v1/student/1?name=max1&email=max1@email.com&dob=2000-01-01
    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable("studentId") Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob)
            throws StudentValidateException {
        studentService.updateStudent(id, name, email, dob);
    }
}
