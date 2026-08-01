package com.artgr.learner.service;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.AccountUpdate;
import com.artgr.learner.data.repository.AccountRepository;
import com.artgr.learner.data.repository.LanguageRepository;
import com.artgr.learner.exceptions.BadRequestException;
import com.artgr.learner.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final LanguageRepository languageRepository;
    private final LanguagePairService languagePairService;

    public AccountService(AccountRepository accountRepository, LanguageRepository languageRepository, LanguagePairService languagePairService) {
        this.accountRepository = accountRepository;
        this.languageRepository = languageRepository;
        this.languagePairService = languagePairService;
    }

    public Account require(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Account " + userId + " not found"));
    }

    // Partial update: null fields on `update` leave the current value
    // unchanged. email/status are not settable here - AccountUpdate has no
    // fields for them (see doc/api/swagger.yaml).
    @Transactional
    public Account update(Long userId, AccountUpdate update) {
        Account account = require(userId);

        if (update.name() != null) {
            account.setName(update.name());
        }
        if (update.uiLang() != null) {
            account.setUiLang(requireLanguage(update.uiLang()));
        }

        boolean pairTouched = update.learnBaseLang() != null || update.learnTargetLang() != null;
        if (pairTouched) {
            Language newBase = update.learnBaseLang() != null ? requireLanguage(update.learnBaseLang()) : account.getLearnBaseLang();
            Language newTarget = update.learnTargetLang() != null ? requireLanguage(update.learnTargetLang()) : account.getLearnTargetLang();
            if (newBase != null && newTarget != null) {
                languagePairService.requireSupported(
                        LanguageCode.valueOf(newBase.getCode()),
                        LanguageCode.valueOf(newTarget.getCode()));
            }
            account.setLearnBaseLang(newBase);
            account.setLearnTargetLang(newTarget);
        }

        return accountRepository.save(account);
    }

    @Transactional
    public void delete(Long userId) {
        if (!accountRepository.existsById(userId)) {
            throw new NotFoundException("Account " + userId + " not found");
        }
        // FK ON DELETE CASCADE removes user_list / list_progress / sessions /
        // attempt (01_schema.sql) - do not hand-delete children here.
        accountRepository.deleteById(userId);
    }

    private Language requireLanguage(LanguageCode code) {
        return languageRepository.findById(code.name())
                .orElseThrow(() -> new BadRequestException("INVALID_LANGUAGE_CODE", "Unknown language code: " + code));
    }
}
