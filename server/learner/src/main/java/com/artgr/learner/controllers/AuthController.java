package com.artgr.learner.controllers;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.presentation.LoginRequest;
import com.artgr.learner.data.presentation.RefreshRequest;
import com.artgr.learner.data.presentation.RegisterRequest;
import com.artgr.learner.data.presentation.ResetCodeRequest;
import com.artgr.learner.data.presentation.ResetConfirmRequest;
import com.artgr.learner.data.presentation.ResetLinkRequest;
import com.artgr.learner.data.presentation.TokenPair;
import com.artgr.learner.data.repository.AccountRepository;
import com.artgr.learner.data.repository.LanguageRepository;
import com.artgr.learner.exceptions.BadRequestException;
import com.artgr.learner.exceptions.ConflictException;
import com.artgr.learner.service.EmailService;
import com.artgr.learner.service.ResetLinkTokenService;
import com.artgr.learner.service.VerificationCodeStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Auth stays STUBBED (doc/CLAUDE.md): login/refresh/logout return canned
// tokens and do not verify anything. No password is hashed or stored -
// everyone who registers/resets is implicitly "authenticated"; there is no
// JWT/session/security filter chain here. Register and reset/* still persist
// the account row and drive the email/verification-code flows.
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String PURPOSE_REGISTER_CONFIRM = "register_confirm";
    private static final String PURPOSE_RESET = "reset";
    private static final String DEFAULT_UI_LANG = "en";

    private final AccountRepository accountRepository;
    private final LanguageRepository languageRepository;
    private final VerificationCodeStore verificationCodeStore;
    private final ResetLinkTokenService resetLinkTokenService;
    private final EmailService emailService;

    public AuthController(
            AccountRepository accountRepository,
            LanguageRepository languageRepository,
            VerificationCodeStore verificationCodeStore,
            ResetLinkTokenService resetLinkTokenService,
            EmailService emailService
    ) {
        this.accountRepository = accountRepository;
        this.languageRepository = languageRepository;
        this.verificationCodeStore = verificationCodeStore;
        this.resetLinkTokenService = resetLinkTokenService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenPair> register(@RequestBody RegisterRequest request) {
        if (accountRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("EMAIL_TAKEN", "An account with this email already exists");
        }

        Account account = new Account();
        account.setEmail(request.email());
        // Hibernate always sends an explicit value on INSERT, so the column's
        // DEFAULT 'en' (01_schema.sql) never fires for JPA-created rows - set
        // it here instead. Only raw-SQL seed rows benefit from the DB default.
        languageRepository.findById(DEFAULT_UI_LANG).ifPresent(account::setUiLang);
        accountRepository.save(account);

        // init_account (name + learning pair) happens via a follow-up PATCH /me
        // call, not here. Not blocking MVP on confirmation being enforced
        // (doc/prompt_2.md) - the code is emailed, but access isn't gated on it.
        String code = verificationCodeStore.generate(PURPOSE_REGISTER_CONFIRM, request.email());
        emailService.sendRegistrationCode(request.email(), account.getName(), code);

        return ResponseEntity.status(HttpStatus.CREATED).body(dummyTokenPair());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(dummyTokenPair());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(dummyTokenPair());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset/request")
    public ResponseEntity<Void> resetRequest(@RequestBody ResetCodeRequest request) {
        // Always 204 regardless of match - account existence must never leak.
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            String code = verificationCodeStore.generate(PURPOSE_RESET, request.email());
            emailService.sendResetCode(request.email(), account.getName(), code);
        });
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset/confirm")
    public ResponseEntity<Void> resetConfirm(@RequestBody ResetConfirmRequest request) {
        boolean verified;
        if (request.code() != null) {
            verified = verificationCodeStore.verifyAndConsume(PURPOSE_RESET, request.email(), request.code());
        } else if (request.token() != null) {
            verified = resetLinkTokenService.verify(request.token(), request.email());
        } else {
            throw new BadRequestException("INVALID_OR_EXPIRED_CODE", "Either code or token is required");
        }
        if (!verified) {
            throw new BadRequestException("INVALID_OR_EXPIRED_CODE", "The reset code or link is invalid or expired");
        }

        // Nothing left to persist: no password hash is stored (see class
        // comment). The verified code/token is consumed above; that's the
        // entire "reset" as far as this stub goes.
        accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("INVALID_OR_EXPIRED_CODE", "The reset code or link is invalid or expired"));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset/link")
    public ResponseEntity<Void> resetLink(@RequestBody ResetLinkRequest request) {
        // Same non-enumeration handling as /reset/request.
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            String token = resetLinkTokenService.generate(request.email());
            String link = resetLinkTokenService.buildLink(token);
            emailService.sendResetLink(request.email(), account.getName(), link);
            emailService.sendFraudNotification(request.email(), account.getName());
        });
        return ResponseEntity.noContent().build();
    }

    private TokenPair dummyTokenPair() {
        return new TokenPair("dummy-access-token", "dummy-refresh-token", 1800);
    }
}
