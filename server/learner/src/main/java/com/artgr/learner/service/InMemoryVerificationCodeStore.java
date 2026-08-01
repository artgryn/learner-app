package com.artgr.learner.service;

import com.artgr.learner.properties.ResetProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Default VerificationCodeStore - in-memory, TTL-checked on read. Fine for a
// single instance / local dev; swap in a Redis-backed VerificationCodeStore
// before running more than one instance (codes would not be shared).
@Component
public class InMemoryVerificationCodeStore implements VerificationCodeStore {

    private final Map<String, Entry> codes = new ConcurrentHashMap<>();
    private final ResetProperties resetProperties;
    private final SecureRandom random = new SecureRandom();

    public InMemoryVerificationCodeStore(ResetProperties resetProperties) {
        this.resetProperties = resetProperties;
    }

    @Override
    public String generate(String purpose, String key) {
        int length = resetProperties.getCodeLength();
        int bound = (int) Math.pow(10, length);
        String code = String.format("%0" + length + "d", random.nextInt(bound));
        Instant expiresAt = Instant.now().plus(resetProperties.getCodeTtl());
        codes.put(cacheKey(purpose, key), new Entry(code, expiresAt));
        return code;
    }

    @Override
    public boolean verifyAndConsume(String purpose, String key, String code) {
        String cacheKey = cacheKey(purpose, key);
        Entry entry = codes.get(cacheKey);
        if (entry == null || entry.expiresAt().isBefore(Instant.now()) || !entry.code().equals(code)) {
            return false;
        }
        codes.remove(cacheKey);
        return true;
    }

    private String cacheKey(String purpose, String key) {
        return purpose + ":" + key;
    }

    private record Entry(String code, Instant expiresAt) {
    }
}
