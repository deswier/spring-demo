package com.example.demo.service;

import com.example.demo.exception.StudentValidateException;
import com.example.demo.dto.StudentDTO;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final MessageSource messageSource;

    @Autowired
    public StudentService(StudentRepository studentRepository, MessageSource messageSource) {
        this.studentRepository = studentRepository;
        this.messageSource = messageSource;
    }

    public Page<Student> getStudents(int page, int pageSize) {
        return studentRepository.findAll(PageRequest.of(page, pageSize));
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public void addNewStudent(StudentDTO student) throws StudentValidateException {
        addNewStudent(new Student(student.getId(), student.getName(), student.getEmail(), student.getDob()));
    }

    public void addNewStudent(Student student) throws StudentValidateException {
        validateStudent(student);

        studentRepository.save(student);
    }

    public void deleteStudent(Long id) throws StudentValidateException {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        } else {
            String message = messageSource.getMessage("student.not.found", new Object[]{id}, LocaleContextHolder.getLocale());
            throw new StudentValidateException(message);
        }
    }

    @Transactional
    public void updateStudent(Long id, String name, String email, LocalDate dob) throws StudentValidateException {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("student.not.found", new Object[]{id}, LocaleContextHolder.getLocale());
                    return new StudentValidateException(message);
                });

        String newName = name != null ? name.trim() : null;
        String newEmail = email != null ? email.trim() : null;

        if (dob != null && !dob.equals(student.getDob())) {
            student.setDob(dob);
        }

        if (newName != null && !newName.isEmpty() && !newName.equals(student.getName())) {
            student.setName(newName);
        }

        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(student.getEmail())) {
            validateStudentByEmail(newEmail);

            student.setEmail(newEmail);
        }
    }

    private void validateStudentByEmail(String email) throws StudentValidateException {
        Optional<Student> studentByEmail = studentRepository.findByEmail(email);

        if (studentByEmail.isPresent()) {
            String message = messageSource.getMessage("student.email.exists", new Object[]{email}, LocaleContextHolder.getLocale());
            throw new StudentValidateException(message, "email");
        }
    }

    private void validateStudent(Student student) throws StudentValidateException {
        validateStudentByEmail(student.getEmail());
    }
}
