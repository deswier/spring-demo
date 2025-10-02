package com.example.demo.student;

import com.example.demo.exception.StudentValidateException;
import com.example.demo.student.dto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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
            throw new StudentValidateException("Student with id: " + id + " does not exist");
        }
    }


    @Transactional
    public void updateStudent(Long id, String name, String email, LocalDate dob) throws StudentValidateException {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentValidateException("Student with id: " + id + " does not exist"));

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
            throw new StudentValidateException("Student with email: " + email + " already exists!", "email");
        }
    }

    private void validateStudent(Student student) throws StudentValidateException {
        validateStudentByEmail(student.getEmail());
    }
}
