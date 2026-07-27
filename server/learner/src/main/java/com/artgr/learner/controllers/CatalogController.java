package com.artgr.learner.controllers;

import com.artgr.learner.data.entity.WordList;
import com.artgr.learner.data.enums.ExerciseType;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.LanguageResponse;
import com.artgr.learner.data.presentation.ListDetail;
import com.artgr.learner.data.presentation.ListSummary;
import com.artgr.learner.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/languages")
    public ResponseEntity<List<LanguageResponse>> languages() {
        List<LanguageResponse> body = catalogService.languages().stream()
                .map(language -> new LanguageResponse(LanguageCode.valueOf(language.getCode()), language.getName()))
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/lists")
    public ResponseEntity<List<ListSummary>> lists(
            @RequestParam LanguageCode target,
            @RequestParam LanguageCode base
    ) {
        List<ListSummary> body = catalogService.listsFor(target, base).stream()
                .map(this::toSummary)
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/lists/{listId}")
    public ResponseEntity<ListDetail> listDetail(@PathVariable Long listId) {
        WordList list = catalogService.requireList(listId);
        List<ExerciseType> allowedExercises = list.getAllowedExercises() == null
                ? null
                : List.copyOf(list.getAllowedExercises());
        return ResponseEntity.ok(new ListDetail(
                list.getId(),
                list.getName(),
                LanguageCode.valueOf(list.getTargetLang().getCode()),
                catalogService.wordCount(list.getId()),
                allowedExercises
        ));
    }

    private ListSummary toSummary(WordList list) {
        return new ListSummary(
                list.getId(),
                list.getName(),
                LanguageCode.valueOf(list.getTargetLang().getCode()),
                catalogService.wordCount(list.getId())
        );
    }
}
