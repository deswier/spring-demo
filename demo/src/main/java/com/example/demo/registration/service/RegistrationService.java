package com.example.demo.registration.service;

import com.example.demo.user.dto.UserDTO;
import com.example.demo.user.model.User;
import com.example.demo.user.role.UserRole;
import com.example.demo.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;

    public String register(UserDTO user) {
        return userService.signUpUser(
                new User(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), UserRole.USER));
    }

}
