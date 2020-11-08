package com.emysilva.etransaction.service.impl;

import com.emysilva.etransaction.model.*;
import com.emysilva.etransaction.repository.PrimaryAccountRepository;
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

    private final PrimaryAccountRepository primaryAccountRepository;

    private final SavingsAccountRepository savingsAccountRepository;

    private final UserService userService;

    private final TransactionService transactionService;

    @Autowired
    public AccountServiceImpl(PrimaryAccountRepository primaryAccountRepository, SavingsAccountRepository savingsAccountRepository, UserService userService, TransactionService transactionService) {
        this.primaryAccountRepository = primaryAccountRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @Override
    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal("0.0"));
        primaryAccount.setAccountNumber(genAccountNumber());
        primaryAccountRepository.save(primaryAccount);

        return primaryAccountRepository.findByAccountNumber(primaryAccount.getAccountNumber());
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

        if (accountType.equalsIgnoreCase("primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountRepository.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction();
            primaryTransaction.setDate(date);
            primaryTransaction.setAmount(amount);
            primaryTransaction.setDescription("Deposit to Current Account");
            primaryTransaction.setStatus("Finished");
            primaryTransaction.setAvailableBalance(primaryAccount.getAccountBalance());
            primaryTransaction.setPrimaryAccount(primaryAccount);
            primaryTransaction.setType("Account");

            transactionService.savePrimaryDepositTransaction(primaryTransaction);
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

        if (accountType.equalsIgnoreCase("primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountRepository.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction();
            primaryTransaction.setDate(date);
            primaryTransaction.setAmount(amount);
            primaryTransaction.setDescription("Withdraw from Current Account");
            primaryTransaction.setStatus("Finished");
            primaryTransaction.setAvailableBalance(primaryAccount.getAccountBalance());
            primaryTransaction.setPrimaryAccount(primaryAccount);
            primaryTransaction.setType("Account");
            transactionService.savePrimaryDepositTransaction(primaryTransaction);
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
