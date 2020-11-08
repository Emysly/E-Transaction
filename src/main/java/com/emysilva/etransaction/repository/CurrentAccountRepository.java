package com.emysilva.etransaction.repository;

import com.emysilva.etransaction.model.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {
    CurrentAccount findByAccountNumber(Integer accountNumber);
}
