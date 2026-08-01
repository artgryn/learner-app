package com.artgr.learner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// Dev/local default (app.mail.enabled=false, the default) - logs instead of
// sending, so local runs and tests never need a real SMTP server.
@Component
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LoggingEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void sendRegistrationCode(String toEmail, String name, String code) {
        log.info("[email:noop] registration code -> {} ({}): {}", toEmail, name, code);
    }

    @Override
    public void sendResetCode(String toEmail, String name, String code) {
        log.info("[email:noop] reset code -> {} ({}): {}", toEmail, name, code);
    }

    @Override
    public void sendResetLink(String toEmail, String name, String link) {
        log.info("[email:noop] reset link -> {} ({}): {}", toEmail, name, link);
    }

    @Override
    public void sendFraudNotification(String toEmail, String name) {
        log.info("[email:noop] fraud notification -> {} ({})", toEmail, name);
    }
}
