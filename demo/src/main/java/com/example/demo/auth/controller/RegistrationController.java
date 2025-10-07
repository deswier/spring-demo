package com.example.demo.auth.controller;


import com.example.demo.auth.service.RegistrationService;
import com.example.demo.user.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    // http://localhost:8080/api/v1/registration/user
    @PostMapping(path = "/user")
    public String register(@RequestBody @Valid UserDTO user) {
        return registrationService.register(user);
    }

    // http://localhost:8080/api/v1/registration/user/confirm
    @GetMapping(path = "/user/confirm")
    public String confirm(@RequestParam String token) {
        return registrationService.confirmToken(token);
    }

}
