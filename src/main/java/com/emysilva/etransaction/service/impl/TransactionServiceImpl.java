package com.emysilva.etransaction.service.impl;

import com.emysilva.etransaction.model.*;
import com.emysilva.etransaction.repository.*;
import com.emysilva.etransaction.service.TransactionService;
import com.emysilva.etransaction.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final UserService userService;
    private final PrimaryTransactionRepository primaryTransactionRepository;
    private final SavingsTransactionRepository savingsTransactionRepository;
    private final PrimaryAccountRepository primaryAccountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final RecipientRepository recipientRepository;

    public TransactionServiceImpl(UserService userService, PrimaryTransactionRepository primaryTransactionRepository, SavingsTransactionRepository savingsTransactionRepository, PrimaryAccountRepository primaryAccountRepository, SavingsAccountRepository savingsAccountRepository, RecipientRepository recipientRepository) {
        this.userService = userService;
        this.primaryTransactionRepository = primaryTransactionRepository;
        this.savingsTransactionRepository = savingsTransactionRepository;
        this.primaryAccountRepository = primaryAccountRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.recipientRepository = recipientRepository;
    }

    @Override
    public List<PrimaryTransaction> findPrimaryTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getPrimaryAccount().getPrimaryTransactionList();
    }

    @Override
    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getSavingsAccount().getSavingsTransactionList();
    }

    @Override
    public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionRepository.save(primaryTransaction);
    }

    @Override
    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionRepository.save(savingsTransaction);
    }

    @Override
    public void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionRepository.save(primaryTransaction);
    }

    @Override
    public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionRepository.save(savingsTransaction);
    }

    @Override
    public void betweenAccountsTransfer(String transferFrom, String transferTo, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception {
        if (transferFrom.equalsIgnoreCase("Primary") && transferTo.equalsIgnoreCase("savings")) {
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));

            primaryAccountRepository.save(primaryAccount);
            savingsAccountRepository.save(savingsAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction();
            primaryTransaction.setPrimaryAccount(primaryAccount);
            primaryTransaction.setAvailableBalance(primaryAccount.getAccountBalance());
            primaryTransaction.setStatus("Finished");
            primaryTransaction.setDescription("Between account transfer from " + transferFrom + " to " + transferTo);
            primaryTransaction.setDate(date);
            primaryTransaction.setType("Account");
            primaryTransaction.setAmount(Double.parseDouble(amount));

            primaryTransactionRepository.save(primaryTransaction);
        }  else if (transferFrom.equalsIgnoreCase("savings") && transferTo.equalsIgnoreCase("current")) {
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountRepository.save(primaryAccount);
            savingsAccountRepository.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction();
            savingsTransaction.setSavingsAccount(savingsAccount);
            savingsTransaction.setAvailableBalance(savingsAccount.getAccountBalance());
            savingsTransaction.setStatus("Finished");
            savingsTransaction.setDescription("Between account transfer from " + transferFrom + " to " + transferTo);
            savingsTransaction.setDate(date);
            savingsTransaction.setType("Account");
            savingsTransaction.setAmount(Double.parseDouble(amount));
            savingsTransactionRepository.save(savingsTransaction);
        } else {
            throw new Exception("Invalid Transfer");
        }
    }

    @Override
    public List<Recipient> findRecipientList(Principal principal) {
        return recipientRepository.findAll().stream()
                .filter(beneficiary -> principal.getName()
                .equals(beneficiary.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveRecipient(Recipient recipient) {
        recipientRepository.save(recipient);
    }

    @Override
    public Recipient findRecipientByName(String recipientName) {
        return recipientRepository.findByName(recipientName);
    }

    @Override
    public void deleteRecipientByName(String recipientName) {
        recipientRepository.deleteByName(recipientName);
    }

    @Override
    public void toSomeoneElseTransfer(Recipient recipient, String accountType, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) {
        if (accountType.equalsIgnoreCase("Primary")) {
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountRepository.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction();
            primaryTransaction.setPrimaryAccount(primaryAccount);
            primaryTransaction.setAvailableBalance(primaryAccount.getAccountBalance());
            primaryTransaction.setStatus("Finished");
            primaryTransaction.setDescription("Transfer to beneficiary "+ recipient.getName());
            primaryTransaction.setDate(date);
            primaryTransaction.setType("Transfer");
            primaryTransaction.setAmount(Double.parseDouble(amount));

            primaryTransactionRepository.save(primaryTransaction);
        } else if (accountType.equalsIgnoreCase("Savings")) {
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountRepository.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction();
            savingsTransaction.setSavingsAccount(savingsAccount);
            savingsTransaction.setAvailableBalance(savingsAccount.getAccountBalance());
            savingsTransaction.setStatus("Finished");
            savingsTransaction.setDescription("Transfer to beneficiary "+ recipient.getName());
            savingsTransaction.setDate(date);
            savingsTransaction.setType("Transfer");
            savingsTransaction.setAmount(Double.parseDouble(amount));
            savingsTransactionRepository.save(savingsTransaction);

            savingsTransactionRepository.save(savingsTransaction);
        }
    }
}
