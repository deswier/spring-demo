package com.example.demo.user.service;

import com.example.demo.auth.token.model.ConfirmationToken;
import com.example.demo.auth.token.service.ConfirmationTokenService;
import com.example.demo.security.encoder.PasswordEncoder;
import com.example.demo.user.exception.UserValidateException;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor // instead of public UserService(UserRepository userRepository, ...)
public class UserService implements UserDetailsService {

    public final static int TOKEN_EXPIRES_MINUTE = 15;
    private final static String USER_NOT_FOUND_MSG = "User with email: %s not found";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(User user) {
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();

        if (userExists) {
            throw new UserValidateException("user.email.exists", List.of(user.getEmail()));
        }

        String encodedPassword = passwordEncoder.bCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(TOKEN_EXPIRES_MINUTE), user);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        // todo send emil

        return token;
    }

    public void enableUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        user.ifPresent(value -> value.setEnabled(true));
    }
}
