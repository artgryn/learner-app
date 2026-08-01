package com.artgr.learner.controllers;

import com.artgr.learner.data.presentation.PublicInitResponse;
import com.artgr.learner.service.LanguagePairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Unauthenticated, pre-login endpoints only - no user context, no
// CurrentUserProvider. Namespaced under /public so more public/bootstrap
// data can be added here later (doc/api/swagger.yaml GET /public/init).
@RestController
@RequestMapping("/public")
public class PublicController {

    private final LanguagePairService languagePairService;

    public PublicController(LanguagePairService languagePairService) {
        this.languagePairService = languagePairService;
    }

    @GetMapping("/init")
    public ResponseEntity<PublicInitResponse> init() {
        return ResponseEntity.ok(new PublicInitResponse(languagePairService.all()));
    }
}
