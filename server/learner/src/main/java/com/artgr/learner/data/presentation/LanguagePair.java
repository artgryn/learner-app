package com.artgr.learner.data.presentation;

// A supported (base, target) combination - sourced from application.yml as a
// TEMPORARY stand-in for coverage-derived pairs (see GET /language-pairs and
// GET /public/init).
public record LanguagePair(LanguageResponse base, LanguageResponse target) {
}
