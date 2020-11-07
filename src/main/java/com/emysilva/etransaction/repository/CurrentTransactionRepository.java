package com.emysilva.etransaction.repository;

import com.emysilva.etransaction.model.CurrentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentTransactionRepository extends JpaRepository<CurrentTransaction, Long> {
}
