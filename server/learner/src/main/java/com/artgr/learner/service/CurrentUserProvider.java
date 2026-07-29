package com.artgr.learner.service;

// Placeholder for "who is the current user" until JWT auth exists (see
// doc/CLAUDE.md - Auth is deliberately not implemented yet). Every caller
// that needs the current user should depend on this interface, not a
// hardcoded id, so there is exactly one place to change - a token-subject-
// derived implementation - once auth lands.
public interface CurrentUserProvider {

    Long currentUserId();
}
