package com.artgr.learner.service;

import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.entity.WordList;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.repository.LanguageRepository;
import com.artgr.learner.data.repository.ListItemRepository;
import com.artgr.learner.data.repository.WordListRepository;
import com.artgr.learner.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    private final LanguageRepository languageRepository;
    private final WordListRepository wordListRepository;
    private final ListItemRepository listItemRepository;

    public CatalogService(LanguageRepository languageRepository, WordListRepository wordListRepository, ListItemRepository listItemRepository) {
        this.languageRepository = languageRepository;
        this.wordListRepository = wordListRepository;
        this.listItemRepository = listItemRepository;
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
}
