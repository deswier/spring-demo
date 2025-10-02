package com.example.demo.student;

import com.example.demo.exception.StudentValidateException;
import com.example.demo.student.dto.StudentDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/registration")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    //example: http://localhost:8080/api/v1/registration/students
    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentService.getStudents();
    }

    @PostMapping
    public ResponseEntity<Void> registerNewStudent(@Valid @RequestBody StudentDTO student) {
        studentService.addNewStudent(student); // может кинуть StudentValidateException

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // example: http://localhost:8080/api/v1/registration/1
    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable("studentId") Long id) throws StudentValidateException {
        studentService.deleteStudent(id);
    }

    // example: http://localhost:8080/api/v1/registration/1?name=max1&email=max1@email.com&dob=2000-01-01
    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable("studentId") Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob)
            throws StudentValidateException {
        studentService.updateStudent(id, name, email, dob);
    }
}
