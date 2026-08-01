package com.artgr.learner.service;

import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.Translation;
import com.artgr.learner.data.entity.WordList;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.ListWord;
import com.artgr.learner.data.repository.LanguageRepository;
import com.artgr.learner.data.repository.ListItemRepository;
import com.artgr.learner.data.repository.TranslationRepository;
import com.artgr.learner.data.repository.WordListRepository;
import com.artgr.learner.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogService {

    private final LanguageRepository languageRepository;
    private final WordListRepository wordListRepository;
    private final ListItemRepository listItemRepository;
    private final TranslationRepository translationRepository;

    public CatalogService(
            LanguageRepository languageRepository,
            WordListRepository wordListRepository,
            ListItemRepository listItemRepository,
            TranslationRepository translationRepository
    ) {
        this.languageRepository = languageRepository;
        this.wordListRepository = wordListRepository;
        this.listItemRepository = listItemRepository;
        this.translationRepository = translationRepository;
    }

    public List<Language> languages() {
        return languageRepository.findAll();
    }

    public List<WordList> listsFor(LanguageCode target, LanguageCode base) {
        return wordListRepository.findByTargetLangWithTranslationCoverage(target.name(), base.name());
    }

    public WordList requireList(Long listId) {
        return wordListRepository.findById(listId)
                .orElseThrow(() -> new NotFoundException("List " + listId + " not found"));
    }

    public int wordCount(Long listId) {
        return (int) listItemRepository.countByListId(listId);
    }

    // @Transactional so the lexeme/translation lazy associations below resolve within one
    // session (open-in-view is off) - the method returns fully-built DTOs, never entities.
    @Transactional(readOnly = true)
    public List<ListWord> wordsFor(Long listId, LanguageCode base) {
        requireList(listId);
        return listItemRepository.findByListIdOrderByPosition(listId).stream()
                .map(item -> {
                    Lexeme lexeme = item.getLexeme();
                    return new ListWord(
                            lexeme.getId(),
                            citationForm(lexeme),
                            lexeme.getPos(),
                            lexeme.getGender(),
                            translationFor(lexeme, base)
                    );
                })
                .toList();
    }

    // Swedish specifics per doc/CLAUDE.md: cite nouns as "en/ett + lemma", verbs as
    // "att + lemma"; never surface a bare noun without its article. Everything else
    // (pronouns, numbers, adjectives, non-Swedish lexemes) is the bare lemma.
    private String citationForm(Lexeme lexeme) {
        String pos = lexeme.getPos();
        String lemma = lexeme.getLemma();
        if ("noun".equals(pos) && lexeme.getGender() != null) {
            return lexeme.getGender() + " " + lemma;
        }
        if ("verb".equals(pos) && "sv".equals(lexeme.getLanguage().getCode())) {
            return "att " + lemma;
        }
        return lemma;
    }

    // `note` carries translation nuance (e.g. "to go, to walk") when present - see
    // doc/CLAUDE.md and doc/app-info/Data/lexeme.md - and takes priority over a bare
    // word-to-word translation, which loses that nuance. Null if neither is available.
    private String translationFor(Lexeme lexeme, LanguageCode base) {
        if (lexeme.getNote() != null) {
            return lexeme.getNote();
        }
        return translationRepository.findByLexemeId(lexeme.getId()).stream()
                .map(translation -> otherSide(translation, lexeme.getId()))
                .filter(other -> other.getLanguage().getCode().equals(base.name()))
                .map(Lexeme::getLemma)
                .findFirst()
                .orElse(null);
    }

    // Translation is an undirected pairwise link - the "other" lexeme is whichever
    // side isn't the one we're translating.
    private Lexeme otherSide(Translation translation, Long lexemeId) {
        return translation.getLexemeA().getId().equals(lexemeId)
                ? translation.getLexemeB()
                : translation.getLexemeA();
    }
}
