package com.artgr.learner.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

// app.reset.* (application.yml) - verification-code settings for password
// reset / registration confirmation (doc/prompt_2.md). codeTtl must stay <= 1h.
// link* fields back the code-less /auth/reset/link flow (ResetLinkTokenService).
@Component
@ConfigurationProperties(prefix = "app.reset")
public class ResetProperties {

    private int codeLength = 4;
    private Duration codeTtl = Duration.ofMinutes(15);
    private Duration linkTtl = Duration.ofMinutes(30);
    // Dev-only default - override via RESET_LINK_SECRET in any real deployment.
    private String linkSecret = "dev-only-insecure-reset-link-secret-change-me";
    private String linkBaseUrl = "https://app.learner.example/reset-password";

    public int getCodeLength() { return codeLength; }
    public void setCodeLength(int codeLength) { this.codeLength = codeLength; }
    public Duration getCodeTtl() { return codeTtl; }
    public void setCodeTtl(Duration codeTtl) { this.codeTtl = codeTtl; }
    public Duration getLinkTtl() { return linkTtl; }
    public void setLinkTtl(Duration linkTtl) { this.linkTtl = linkTtl; }
    public String getLinkSecret() { return linkSecret; }
    public void setLinkSecret(String linkSecret) { this.linkSecret = linkSecret; }
    public String getLinkBaseUrl() { return linkBaseUrl; }
    public void setLinkBaseUrl(String linkBaseUrl) { this.linkBaseUrl = linkBaseUrl; }
}
