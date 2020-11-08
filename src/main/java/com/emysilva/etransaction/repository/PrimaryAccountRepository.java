package com.emysilva.etransaction.repository;

import com.emysilva.etransaction.model.PrimaryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrimaryAccountRepository extends JpaRepository<PrimaryAccount, Long> {
    PrimaryAccount findByAccountNumber(Integer accountNumber);
}
