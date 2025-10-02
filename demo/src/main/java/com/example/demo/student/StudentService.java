package com.example.demo.student;

import com.example.demo.exception.StudentValidateException;
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
    public void updateStudent(Long id, String name, String email, LocalDate dob) throws StudentValidateException{
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
            Optional<Student>  studentByEmail = studentRepository.findByEmail(newEmail);

            if (studentByEmail.isPresent() && !studentByEmail.get().getId().equals(student.getId())) {
                throw new StudentValidateException("Student with email: " + newEmail + " already exists");
            }

            student.setEmail(newEmail);
        }
    }

    private void validateStudent(Student student) throws StudentValidateException {
        if (student.getEmail() == null || student.getEmail().isEmpty()) {
            throw new StudentValidateException("Email can't be empty!");
        }

        if (student.getName() == null || student.getName().isEmpty()) {
            throw new StudentValidateException("Name can't be empty!");
        }

        Optional<Student> studentByEmail = studentRepository.findByEmail(student.getEmail());

        if (studentByEmail.isPresent()) {
            throw new StudentValidateException("Student with email: " + student.getEmail() + " already exists!");
        }
    }
}
