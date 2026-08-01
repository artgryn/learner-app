package com.artgr.learner.service;

// Side-effect of auth/account flows - there is no public /email endpoint.
// Implementations send asynchronously (see AsyncConfig) so the request
// thread never blocks on SMTP; failures are logged, not surfaced mid-request.
public interface EmailService {

    void sendRegistrationCode(String toEmail, String name, String code);

    void sendResetCode(String toEmail, String name, String code);

    void sendResetLink(String toEmail, String name, String link);

    void sendFraudNotification(String toEmail, String name);
}
