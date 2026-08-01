package com.artgr.learner.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// app.mail.* (application.yml). enabled=false (default/local) selects
// LoggingEmailService (no-op) instead of the real SmtpEmailService, so local
// runs and tests never need a real SMTP server.
@Component
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {

    private boolean enabled = false;
    private String fromAddress = "no-reply@learner.app";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
}
