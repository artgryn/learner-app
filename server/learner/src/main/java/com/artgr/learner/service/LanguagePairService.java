package com.artgr.learner.service;

import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.LanguagePair;
import com.artgr.learner.data.presentation.LanguageResponse;
import com.artgr.learner.data.repository.LanguageRepository;
import com.artgr.learner.exceptions.BadRequestException;
import com.artgr.learner.properties.LanguagePairsProperties;
import org.springframework.stereotype.Service;

import java.util.List;

// Pairs are config-driven (app.language-pairs) - a TEMPORARY stand-in for
// coverage-derived pairs; the real long-term constraint is translation
// coverage in the data (doc/api/swagger.yaml GET /language-pairs,
// GET /public/init). Backs both the secured and public endpoints, and is the
// validation source for PATCH /me's learnBaseLang/learnTargetLang check.
@Service
public class LanguagePairService {

    private final LanguagePairsProperties properties;
    private final LanguageRepository languageRepository;

    public LanguagePairService(LanguagePairsProperties properties, LanguageRepository languageRepository) {
        this.properties = properties;
        this.languageRepository = languageRepository;
    }

    public List<LanguagePair> all() {
        return properties.getLanguagePairs().stream()
                .map(pair -> new LanguagePair(toResponse(pair.getBase()), toResponse(pair.getTarget())))
                .toList();
    }

    public void requireSupported(LanguageCode base, LanguageCode target) {
        boolean supported = properties.getLanguagePairs().stream()
                .anyMatch(pair -> pair.getBase().equals(base.name()) && pair.getTarget().equals(target.name()));
        if (!supported) {
            throw new BadRequestException("UNSUPPORTED_PAIR",
                    "Unsupported language pair: base=" + base + ", target=" + target);
        }
    }

    private LanguageResponse toResponse(String code) {
        Language language = languageRepository.findById(code)
                .orElseThrow(() -> new IllegalStateException(
                        "app.language-pairs references unknown language code '" + code + "'"));
        return new LanguageResponse(LanguageCode.valueOf(language.getCode()), language.getName());
    }
}
