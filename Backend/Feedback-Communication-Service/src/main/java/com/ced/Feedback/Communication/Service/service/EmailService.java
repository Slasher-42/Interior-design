package com.ced.Feedback.Communication.Service.service;

import com.ced.Feedback.Communication.Service.domain.EmailLog;
import com.ced.Feedback.Communication.Service.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Every outbound "email" is always persisted to the email log so it can be inspected via
 * GET /email-logs without any SMTP setup. Real SMTP dispatch only happens when app.mail.enabled
 * is explicitly turned on with real spring.mail.* settings - by default this service runs (and is
 * fully testable end-to-end) with zero external mail dependencies.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@idsms.local}")
    private String fromAddress;

    @Transactional
    public void send(String to, String subject, String body) {
        boolean success = true;
        if (mailEnabled) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
            } catch (MailException e) {
                log.warn("Failed to send email to {}: {}", to, e.getMessage());
                success = false;
            }
        } else {
            log.info("[SIMULATED EMAIL] to={} subject={}", to, subject);
        }

        emailLogRepository.save(EmailLog.builder()
                .recipientEmail(to)
                .subject(subject)
                .body(body)
                .success(success)
                .build());
    }

    public Page<EmailLog> list(Pageable pageable) {
        return emailLogRepository.findAll(pageable);
    }
}
