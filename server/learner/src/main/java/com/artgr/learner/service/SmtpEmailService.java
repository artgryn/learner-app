package com.artgr.learner.service;

import com.artgr.learner.properties.MailProperties;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

// Real sender - only active when app.mail.enabled=true (see MailProperties).
// Each send is @Async so the caller's request thread never blocks on SMTP;
// failures are logged here, never surfaced back to the caller.
@Component
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;

    public SmtpEmailService(JavaMailSender mailSender, TemplateEngine templateEngine, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
    }

    @Async
    @Override
    public void sendRegistrationCode(String toEmail, String name, String code) {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("code", code);
        send(toEmail, "Confirm your registration", "registration-code", ctx);
    }

    @Async
    @Override
    public void sendResetCode(String toEmail, String name, String code) {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("code", code);
        send(toEmail, "Your password reset code", "reset-code", ctx);
    }

    @Async
    @Override
    public void sendResetLink(String toEmail, String name, String link) {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        ctx.setVariable("link", link);
        send(toEmail, "Reset your password", "reset-link", ctx);
    }

    @Async
    @Override
    public void sendFraudNotification(String toEmail, String name) {
        Context ctx = new Context();
        ctx.setVariable("name", name);
        send(toEmail, "Was this you?", "fraud-notification", ctx);
    }

    private void send(String toEmail, String subject, String template, Context ctx) {
        try {
            String html = templateEngine.process("email/" + template, ctx);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(toEmail);
            helper.setFrom(mailProperties.getFromAddress());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send '{}' email to {}", template, toEmail, e);
        }
    }
}
