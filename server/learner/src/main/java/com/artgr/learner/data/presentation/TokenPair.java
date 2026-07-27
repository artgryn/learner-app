package com.artgr.learner.data.presentation;

public record TokenPair(String accessToken, String refreshToken, Integer expiresIn) {
}
