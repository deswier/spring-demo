package com.example.demo.registration.controller;


import com.example.demo.registration.service.RegistrationService;
import com.example.demo.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    // http://localhost:8080/api/v1/registration/user
    @PostMapping(path = "/user")
    public String register(@RequestBody UserDTO user) {
        return registrationService.register(user);
    }

}
