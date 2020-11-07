package com.emysilva.etransaction.service;

import com.emysilva.etransaction.model.Beneficiary;
import com.emysilva.etransaction.model.CurrentTransaction;
import com.emysilva.etransaction.model.SavingsAccount;
import com.emysilva.etransaction.model.SavingsTransaction;

import java.security.Principal;
import java.util.List;

public interface TransactionService {
    List<CurrentTransaction> findCurrentTransactionList(String username);

    List<SavingsTransaction> findSavingsTransactionList(String username);

    void saveCurrentDepositTransaction(CurrentTransaction currentTransaction);

    void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction);

    void savePrimaryWithdrawTransaction(CurrentTransaction currentTransaction);
    void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction);

    void betweenAccountsTransfer(String transferFrom, String transferTo, String amount, CurrentTransaction currentTransaction, SavingsAccount savingsAccount) throws Exception;

    List<Beneficiary> findRecipientList(Principal principal);

    void saveRecipient(Beneficiary beneficiary);

    Beneficiary findBeneficiaryByName(String beneficiaryName);

    void deleteBeneficiaryByName(String beneficiaryName);

    void toSomeoneElseTransfer(Beneficiary beneficiary, String accountType, String amount, CurrentTransaction currentTransaction, SavingsAccount savingsAccount);
}
