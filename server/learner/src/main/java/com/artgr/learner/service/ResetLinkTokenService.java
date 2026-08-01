package com.artgr.learner.service;

import com.artgr.learner.properties.ResetProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;

// Stateless, signed password-reset link token (doc/prompt_2.md - the
// "code-less path"). NOT a JWT/session/auth token: single-purpose, short-lived,
// no storage, verifiable offline. Hand-rolled HMAC-SHA256 rather than a JWT
// library, deliberately, to keep this narrowly scoped to reset links and not
// resemble real auth infra (see doc/CLAUDE.md - auth stays stubbed).
@Service
public class ResetLinkTokenService {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final ResetProperties resetProperties;

    public ResetLinkTokenService(ResetProperties resetProperties) {
        this.resetProperties = resetProperties;
    }

    public String generate(String email) {
        long expiresAt = Instant.now().plus(resetProperties.getLinkTtl()).getEpochSecond();
        String payload = email + "|" + expiresAt;
        return ENCODER.encodeToString(payload.getBytes(StandardCharsets.UTF_8)) + "." + sign(payload);
    }

    public boolean verify(String token, String expectedEmail) {
        try {
            int dot = token.indexOf('.');
            if (dot < 0) {
                return false;
            }
            String payload = new String(DECODER.decode(token.substring(0, dot)), StandardCharsets.UTF_8);
            String signature = token.substring(dot + 1);
            if (!sign(payload).equals(signature)) {
                return false;
            }
            String[] parts = payload.split("\\|", 2);
            if (parts.length != 2 || !parts[0].equals(expectedEmail)) {
                return false;
            }
            return Instant.now().getEpochSecond() <= Long.parseLong(parts[1]);
        } catch (RuntimeException e) {
            // Malformed token (bad base64, bad format) - treat as invalid, not an error.
            return false;
        }
    }

    public String buildLink(String token) {
        return resetProperties.getLinkBaseUrl() + "?token=" + token;
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(resetProperties.getLinkSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            return ENCODER.encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to sign reset link token", e);
        }
    }
}
