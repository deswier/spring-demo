package com.example.demo.auth.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final MessageSource messageSource;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom("alinamikh1999@gmail.com");
            helper.setSubject(messageSource.getMessage("confirm.email", null, LocaleContextHolder.getLocale()));
            helper.setText(email, true);
            helper.setTo(to);
            mailSender.send(message);
        } catch (MessagingException e) {
            LOG.error("Failed to send email!");
            throw new IllegalStateException("Failed to send email!");
        }

    }

}
