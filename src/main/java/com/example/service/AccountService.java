package com.example.service;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import com.example.exception.ResourceNotFoundException;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private MessageService messageService;

    @Autowired
    public AccountService(AccountRepository accountRepository, MessageService messageService){
        this.messageService = messageService;
        this.accountRepository = accountRepository;
    }

    public Account register(Account newAccount){
        return accountRepository.save(newAccount);
    }

    public boolean usernameExists(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }

    public Account login(String username, String password) throws AuthenticationException {
        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new AuthenticationException("Account not found"));
        
        if (!account.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        return account;
    }

}
