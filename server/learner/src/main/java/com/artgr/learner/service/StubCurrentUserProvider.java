package com.artgr.learner.service;

import org.springframework.stereotype.Component;

// No JWT/Spring Security yet - always resolves to the seeded demo account
// (see doc/api/02_seed.sql). Do not add token parsing here; replace this
// class entirely with a SecurityContext-backed implementation once auth is
// implemented.
@Component
public class StubCurrentUserProvider implements CurrentUserProvider {

    private static final Long DEMO_USER_ID = 1L;

    @Override
    public Long currentUserId() {
        return DEMO_USER_ID;
    }
}
