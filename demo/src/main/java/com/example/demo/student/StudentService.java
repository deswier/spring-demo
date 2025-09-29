package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void addNewStudent(Student student) {
        Optional<Student> studentByEmail = studentRepository.findByEmail(student.getEmail());

        if (studentByEmail.isPresent()) {
            throw new IllegalStateException("Student with email: " + student.getEmail() + " already exists");
        }

        studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Student with id: " + id + " does not exist");
        }
    }


    @Transactional
    public void updateStudent(Long id, String name, String email) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Student with id: " + id + " does not exist"));

        String newName  = name  != null ? name.trim()  : null;
        String newEmail = email != null ? email.trim() : null;

        if (newName != null && !newName.isEmpty() && !newName.equals(student.getName())) {
            student.setName(newName);
        }

        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(student.getEmail())) {
            studentRepository.findByEmail(newEmail).ifPresent(other -> {
                if (!other.getId().equals(student.getId())) {
                    throw new IllegalStateException("Student with email: " + newEmail + " already exists");
                }
            });

            student.setEmail(newEmail);
        }
    }
}
