package com.example.demo.config;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.JANUARY;
import static java.time.Month.JULY;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
        return args -> {
            Student max = new Student("Max", "max@gmail.com", LocalDate.of(1970, JANUARY, 1));
            Student ann = new Student("Ann", "ann@gmail.com", LocalDate.of(1980, JANUARY, 1));
            Student kate = new Student("Kate", "kate@gmail.com", LocalDate.of(1980, JULY, 1));
            Student john1 = new Student("John1", "john1@gmail.com", LocalDate.of(1991, JANUARY, 1));
            Student john2 = new Student("John2", "john2@gmail.com", LocalDate.of(1992, JANUARY, 1));
            Student john3 = new Student("John3", "john3@gmail.com", LocalDate.of(1993, JANUARY, 1));
            Student john4 = new Student("John4", "john4@gmail.com", LocalDate.of(1994, JANUARY, 1));
            Student john5 = new Student("John5", "john5@gmail.com", LocalDate.of(1995, JANUARY, 1));
            Student john6 = new Student("John6", "john6@gmail.com", LocalDate.of(1996, JANUARY, 1));
            Student john7 = new Student("John7", "john7@gmail.com", LocalDate.of(1997, JANUARY, 1));
            Student john8 = new Student("John8", "john8@gmail.com", LocalDate.of(1998, JANUARY, 1));
            Student john9 = new Student("John9", "john9@gmail.com", LocalDate.of(1999, JANUARY, 1));
            Student john10 = new Student("John10", "john10@gmail.com", LocalDate.of(2000, JANUARY, 1));
            Student john11 = new Student("John11", "john11@gmail.com", LocalDate.of(2001, JANUARY, 1));
            Student john12 = new Student("John12", "john12@gmail.com", LocalDate.of(2002, JANUARY, 1));
            Student john13 = new Student("John13", "john13@gmail.com", LocalDate.of(2003, JANUARY, 1));
            Student john14 = new Student("John14", "john14@gmail.com", LocalDate.of(2004, JANUARY, 1));
            Student john15 = new Student("John15", "john15@gmail.com", LocalDate.of(2005, JANUARY, 1));
            Student john16 = new Student("John16", "john16@gmail.com", LocalDate.of(2006, JANUARY, 1));
            Student john17 = new Student("John17", "john17@gmail.com", LocalDate.of(2007, JANUARY, 1));
            Student john18 = new Student("John18", "john18@gmail.com", LocalDate.of(2008, JANUARY, 1));
            Student john19 = new Student("John19", "john19@gmail.com", LocalDate.of(2009, JANUARY, 1));
            Student john20 = new Student("John20", "john20@gmail.com", LocalDate.of(2010, JANUARY, 1));

            studentRepository.saveAll(List.of(max, ann, kate, john1, john2, john3, john4, john5, john6, john7, john8, john9, john10, john11, john12, john13, john14, john15, john16, john17, john18, john19, john20));
        };
    }

}
