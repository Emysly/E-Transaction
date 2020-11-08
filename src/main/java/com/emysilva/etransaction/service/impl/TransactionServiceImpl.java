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
    private final CurrentTransactionRepository currentTransactionRepository;
    private final SavingsTransactionRepository savingsTransactionRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    public TransactionServiceImpl(UserService userService, CurrentTransactionRepository currentTransactionRepository, SavingsTransactionRepository savingsTransactionRepository, CurrentAccountRepository currentAccountRepository, SavingsAccountRepository savingsAccountRepository, BeneficiaryRepository beneficiaryRepository) {
        this.userService = userService;
        this.currentTransactionRepository = currentTransactionRepository;
        this.savingsTransactionRepository = savingsTransactionRepository;
        this.currentAccountRepository = currentAccountRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @Override
    public List<CurrentTransaction> findCurrentTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getCurrentAccount().getCurrentTransactionList();
    }

    @Override
    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getSavingsAccount().getSavingsTransactionList();
    }

    @Override
    public void saveCurrentDepositTransaction(CurrentTransaction currentTransaction) {
        currentTransactionRepository.save(currentTransaction);
    }

    @Override
    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionRepository.save(savingsTransaction);
    }

    @Override
    public void saveCurrentWithdrawTransaction(CurrentTransaction currentTransaction) {
        currentTransactionRepository.save(currentTransaction);
    }

    @Override
    public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionRepository.save(savingsTransaction);
    }

    @Override
    public void betweenAccountsTransfer(String transferFrom, String transferTo, String amount, CurrentAccount currentAccount, SavingsAccount savingsAccount) throws Exception {
        if (transferFrom.equalsIgnoreCase("current") && transferTo.equalsIgnoreCase("savings")) {
            currentAccount.setAccountBalance(currentAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));

            currentAccountRepository.save(currentAccount);
            savingsAccountRepository.save(savingsAccount);

            Date date = new Date();

            CurrentTransaction currentTransaction = new CurrentTransaction();
            currentTransaction.setCurrentAccount(currentAccount);
            currentTransaction.setAvailableBalance(currentAccount.getAccountBalance());
            currentTransaction.setStatus("Finished");
            currentTransaction.setDescription("Between account transfer from " + transferFrom + " to " + transferTo);
            currentTransaction.setDate(date);
            currentTransaction.setType("Account");
            currentTransaction.setAmount(Double.parseDouble(amount));

            currentTransactionRepository.save(currentTransaction);
        }  else if (transferFrom.equalsIgnoreCase("savings") && transferTo.equalsIgnoreCase("current")) {
            currentAccount.setAccountBalance(currentAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            currentAccountRepository.save(currentAccount);
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
    public List<Beneficiary> findRecipientList(Principal principal) {
        return beneficiaryRepository.findAll().stream()
                .filter(beneficiary -> principal.getName()
                .equals(beneficiary.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveRecipient(Beneficiary beneficiary) {
        beneficiaryRepository.save(beneficiary);
    }

    @Override
    public Beneficiary findBeneficiaryByName(String beneficiaryName) {
        return beneficiaryRepository.findByName(beneficiaryName);
    }

    @Override
    public void deleteBeneficiaryByName(String beneficiaryName) {
        beneficiaryRepository.deleteByName(beneficiaryName);
    }

    @Override
    public void toSomeoneElseTransfer(Beneficiary beneficiary, String accountType, String amount, CurrentAccount currentAccount, SavingsAccount savingsAccount) {
        if (accountType.equalsIgnoreCase("Primary")) {
            currentAccount.setAccountBalance(currentAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            currentAccountRepository.save(currentAccount);

            Date date = new Date();

            CurrentTransaction currentTransaction = new CurrentTransaction();
            currentTransaction.setCurrentAccount(currentAccount);
            currentTransaction.setAvailableBalance(currentAccount.getAccountBalance());
            currentTransaction.setStatus("Finished");
            currentTransaction.setDescription("Transfer to beneficiary "+ beneficiary.getName());
            currentTransaction.setDate(date);
            currentTransaction.setType("Transfer");
            currentTransaction.setAmount(Double.parseDouble(amount));

            currentTransactionRepository.save(currentTransaction);
        } else if (accountType.equalsIgnoreCase("Savings")) {
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountRepository.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction();
            savingsTransaction.setSavingsAccount(savingsAccount);
            savingsTransaction.setAvailableBalance(savingsAccount.getAccountBalance());
            savingsTransaction.setStatus("Finished");
            savingsTransaction.setDescription("Transfer to beneficiary "+ beneficiary.getName());
            savingsTransaction.setDate(date);
            savingsTransaction.setType("Transfer");
            savingsTransaction.setAmount(Double.parseDouble(amount));
            savingsTransactionRepository.save(savingsTransaction);

            savingsTransactionRepository.save(savingsTransaction);
        }
    }
}
