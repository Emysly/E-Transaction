package com.emysilva.etransaction.service.impl;

import com.emysilva.etransaction.model.*;
import com.emysilva.etransaction.repository.CurrentAccountRepository;
import com.emysilva.etransaction.repository.SavingsAccountRepository;
import com.emysilva.etransaction.service.AccountService;
import com.emysilva.etransaction.service.TransactionService;
import com.emysilva.etransaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {
    private static Integer baseAccountNumber = 1234567890;

    private final CurrentAccountRepository currentAccountRepository;

    private final SavingsAccountRepository savingsAccountRepository;

    private final UserService userService;

    private final TransactionService transactionService;

    @Autowired
    public AccountServiceImpl(CurrentAccountRepository currentAccountRepository, SavingsAccountRepository savingsAccountRepository, UserService userService, TransactionService transactionService) {
        this.currentAccountRepository = currentAccountRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @Override
    public CurrentAccount createCurrentAccount() {
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setAccountBalance(new BigDecimal("0.0"));
        currentAccount.setAccountNumber(genAccountNumber());
        currentAccountRepository.save(currentAccount);

        return currentAccountRepository.findByAccountNumber(currentAccount.getAccountNumber());
    }

    @Override
    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal("0.0"));
        savingsAccount.setAccountNumber(genAccountNumber());
        savingsAccountRepository.save(savingsAccount);

        return savingsAccountRepository.findByAccountNumber(savingsAccount.getAccountNumber());
    }

    @Override
    public void deposit(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("current")) {
            CurrentAccount currentAccount = user.getCurrentAccount();
            currentAccount.setAccountBalance(currentAccount.getAccountBalance().add(new BigDecimal(amount)));
            currentAccountRepository.save(currentAccount);

            Date date = new Date();

            CurrentTransaction currentTransaction = new CurrentTransaction();
            currentTransaction.setDate(date);
            currentTransaction.setAmount(amount);
            currentTransaction.setDescription("Deposit to Current Account");
            currentTransaction.setStatus("Finished");
            currentTransaction.setAvailableBalance(currentAccount.getAccountBalance());
            currentTransaction.setCurrentAccount(currentAccount);
            currentTransaction.setType("Account");

            transactionService.saveCurrentDepositTransaction(currentTransaction);
        }
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
        savingsAccountRepository.save(savingsAccount);

        Date date = new Date();

        SavingsTransaction savingsTransaction = new SavingsTransaction();
        savingsTransaction.setAmount(amount);
        savingsTransaction.setAvailableBalance(savingsAccount.getAccountBalance());
        savingsTransaction.setDate(date);
        savingsTransaction.setDescription("Deposit to Savings Account");
        savingsTransaction.setSavingsAccount(savingsAccount);
        savingsTransaction.setStatus("Finished");
        savingsTransaction.setType("Account");

        transactionService.saveSavingsDepositTransaction(savingsTransaction);
    }

    @Override
    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("current")) {
            CurrentAccount currentAccount = user.getCurrentAccount();
            currentAccount.setAccountBalance(currentAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            currentAccountRepository.save(currentAccount);

            Date date = new Date();

            CurrentTransaction currentTransaction = new CurrentTransaction();
            currentTransaction.setDate(date);
            currentTransaction.setAmount(amount);
            currentTransaction.setDescription("Withdraw from Current Account");
            currentTransaction.setStatus("Finished");
            currentTransaction.setAvailableBalance(currentAccount.getAccountBalance());
            currentTransaction.setCurrentAccount(currentAccount);
            currentTransaction.setType("Account");
            transactionService.saveCurrentDepositTransaction(currentTransaction);
        }
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
        savingsAccountRepository.save(savingsAccount);

        Date date = new Date();

        SavingsTransaction savingsTransaction = new SavingsTransaction();
        savingsTransaction.setAmount(amount);
        savingsTransaction.setAvailableBalance(savingsAccount.getAccountBalance());
        savingsTransaction.setDate(date);
        savingsTransaction.setDescription("Withdraw from Savings Account");
        savingsTransaction.setSavingsAccount(savingsAccount);
        savingsTransaction.setStatus("Finished");
        savingsTransaction.setType("Account");

        transactionService.saveSavingsDepositTransaction(savingsTransaction);

    }

    private Integer genAccountNumber() {
        return ++baseAccountNumber;
    }
}
