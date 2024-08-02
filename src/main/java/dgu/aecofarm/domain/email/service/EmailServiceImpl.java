package dgu.aecofarm.domain.email.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendAuthCode(String email, String authCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[AECOfarm] Email Verification Code");
            mimeMessageHelper.setText("Your verification code is: " + authCode, false);  // HTML이 아니라면 false로 설정
            javaMailSender.send(mimeMessage);
            log.info("Auth code sent to email: " + email);
        } catch (jakarta.mail.MessagingException e) {
            log.error("Failed to send auth code to email: " + email, e);
            throw new RuntimeException("Failed to send auth code", e);
        }
    }
}
