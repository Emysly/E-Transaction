package com.emysilva.etransaction.service;

import com.emysilva.etransaction.model.CurrentAccount;
import com.emysilva.etransaction.model.SavingsAccount;

import java.security.Principal;

public interface AccountService {
    CurrentAccount createCurrentAccount();
    SavingsAccount createSavingsAccount();
    void deposit(String accountType, double amount, Principal principal);
    void withdraw(String accountType, double amount, Principal principal);
}
