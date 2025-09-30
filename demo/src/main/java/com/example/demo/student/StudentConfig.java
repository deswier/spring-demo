package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.JANUARY;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
        return args -> {
            Student max = new Student("Max", "max@gmail.com", LocalDate.of(1970, JANUARY, 1));
            Student ann = new Student("Ann", "ann@gmail.com", LocalDate.of(1980, JANUARY, 1));

            studentRepository.saveAll(List.of(max, ann));
        };
    }

}
