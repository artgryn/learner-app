package com.artgr.learner.data.mappers;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.enums.AccountStatus;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.AccountView;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountView toDto(Account account) {
        return new AccountView(
                account.getId(),
                account.getEmail(),
                account.getName(),
                toCode(account.getUiLang()),
                toCode(account.getLearnBaseLang()),
                toCode(account.getLearnTargetLang()),
                AccountStatus.valueOf(account.getStatus())
        );
    }

    private LanguageCode toCode(Language language) {
        return language == null ? null : LanguageCode.valueOf(language.getCode());
    }
}
