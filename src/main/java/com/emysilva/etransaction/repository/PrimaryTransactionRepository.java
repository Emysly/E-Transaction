package com.emysilva.etransaction.repository;

import com.emysilva.etransaction.model.PrimaryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrimaryTransactionRepository extends JpaRepository<PrimaryTransaction, Long> {
}
