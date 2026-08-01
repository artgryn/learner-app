package com.artgr.learner.controllers;

import com.artgr.learner.data.mappers.AccountMapper;
import com.artgr.learner.data.presentation.AccountUpdate;
import com.artgr.learner.data.presentation.AccountView;
import com.artgr.learner.service.AccountService;
import com.artgr.learner.service.CurrentUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class AccountController {

    private final AccountService accountService;
    private final CurrentUserProvider currentUserProvider;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, CurrentUserProvider currentUserProvider, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.currentUserProvider = currentUserProvider;
        this.accountMapper = accountMapper;
    }

    @GetMapping
    public ResponseEntity<AccountView> get() {
        Long userId = currentUserProvider.currentUserId();
        return ResponseEntity.ok(accountMapper.toDto(accountService.require(userId)));
    }

    @PatchMapping
    public ResponseEntity<AccountView> update(@RequestBody AccountUpdate update) {
        Long userId = currentUserProvider.currentUserId();
        return ResponseEntity.ok(accountMapper.toDto(accountService.update(userId, update)));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete() {
        Long userId = currentUserProvider.currentUserId();
        accountService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
