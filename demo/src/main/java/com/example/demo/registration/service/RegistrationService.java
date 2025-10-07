package com.example.demo.registration.service;

import com.example.demo.registration.email.EmailSender;
import com.example.demo.registration.token.model.ConfirmationToken;
import com.example.demo.registration.token.service.ConfirmationTokenService;
import com.example.demo.user.dto.UserDTO;
import com.example.demo.user.model.User;
import com.example.demo.user.role.UserRole;
import com.example.demo.user.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationService.class);
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final MessageSource messageSource;

    public String register(UserDTO user) {
        String token =  userService.signUpUser(
                new User(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), UserRole.USER));

        String link = "http://localhost:8080/api/v1/registration/user/confirm?token=" + token;
        emailSender.send(user.getEmail(), buildEmail(user.getFirstName() + " " + user.getLastName(), link));

        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token " + token + " not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed for token " + token);
        }

        String message;

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            LOG.warn("Token {} expired", token);
            message = "link.is.expired";
        } else {
            confirmationTokenService.setConfirmedAt(confirmationToken);
            userService.enableUser(confirmationToken.getUser().getEmail());
            message = "email.confirmed.successfully";
        }

        return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
    }

    private String buildEmail(String name, String link) {
        var locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.registration.subject", null, locale);
        String greeting = messageSource.getMessage("email.registration.greeting", new Object[]{name}, locale);
        String body = messageSource.getMessage("email.registration.body", null, locale);
        String activateNow = messageSource.getMessage("email.registration.activate.now", null, locale);
        String expirationMessage = messageSource.getMessage("email.registration.expiration.message", new Object[]{UserService.TOKEN_EXPIRES_MINUTE}, locale);
        String farewell = messageSource.getMessage("email.registration.farewell", null, locale);

        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
               "\n" +
               "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
               "\n" +
               "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
               "    <tbody><tr>\n" +
               "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
               "        \n" +
               "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
               "          <tbody><tr>\n" +
               "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
               "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
               "                  <tbody><tr>\n" +
               "                    <td style=\"padding-left:10px\">\n" +
               "                  \n" +
               "                    </td>\n" +
               "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
               "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">" + subject + "</span>\n" +
               "                    </td>\n" +
               "                  </tr>\n" +
               "                </tbody></table>\n" +
               "              </a>\n" +
               "            </td>\n" +
               "          </tr>\n" +
               "        </tbody></table>\n" +
               "        \n" +
               "      </td>\n" +
               "    </tr>\n" +
               "  </tbody></table>\n" +
               "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
               "    <tbody><tr>\n" +
               "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
               "      <td>\n" +
               "        \n" +
               "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
               "                  <tbody><tr>\n" +
               "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
               "                  </tr>\n" +
               "                </tbody></table>\n" +
               "        \n" +
               "      </td>\n" +
               "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
               "    </tr>\n" +
               "  </tbody></table>\n" +
               "\n" +
               "\n" +
               "\n" +
               "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
               "    <tbody><tr>\n" +
               "      <td height=\"30\"><br></td>\n" +
               "    </tr>\n" +
               "    <tr>\n" +
               "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
               "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
               "        \n" +
               "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">" + greeting + "</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " + body + " </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">" + activateNow + "</a> </p></blockquote>\n " + expirationMessage + " <p>" + farewell + "</p>" +
               "        \n" +
               "      </td>\n" +
               "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
               "    </tr>\n" +
               "    <tr>\n" +
               "      <td height=\"30\"><br></td>\n" +
               "    </tr>\n" +
               "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
               "\n" +
               "</div></div>";
    }
}
