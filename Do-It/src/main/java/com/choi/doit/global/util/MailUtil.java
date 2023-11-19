package com.choi.doit.global.util;

import com.choi.doit.domain.user.exception.UserErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailUtil {
    private final JavaMailSender javaMailSender;

    public void sendMail(String to, String subject, String template) throws RestApiException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(template, true);

            javaMailSender.send(mimeMessage);

            log.info("[Mail] Email sent successfully. -- " + to);
        } catch (MessagingException e) {
            log.error("[Mail] Email sending failed. -- " + to);
            throw new RestApiException(UserErrorCode.EMAIL_SENDING_FAILED);
        }
    }
}
