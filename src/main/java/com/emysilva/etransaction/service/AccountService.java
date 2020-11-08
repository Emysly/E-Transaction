package com.emysilva.etransaction.service;

import com.emysilva.etransaction.model.PrimaryAccount;
import com.emysilva.etransaction.model.SavingsAccount;

import java.security.Principal;

public interface AccountService {
    PrimaryAccount createPrimaryAccount();
    SavingsAccount createSavingsAccount();
    void deposit(String accountType, double amount, Principal principal);
    void withdraw(String accountType, double amount, Principal principal);
}
