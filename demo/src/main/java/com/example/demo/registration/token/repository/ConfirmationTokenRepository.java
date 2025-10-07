package com.example.demo.registration.token.repository;

import com.example.demo.registration.token.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long>  {

    Optional<ConfirmationToken> findByToken(String token);

}
