package com.artgr.learner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// Enables @Async (SmtpEmailService) so email sends never block the request
// thread (doc/prompt_2.md).
@Configuration
@EnableAsync
public class AsyncConfig {
}
