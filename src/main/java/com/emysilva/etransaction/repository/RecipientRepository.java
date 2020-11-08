package com.emysilva.etransaction.repository;

import com.emysilva.etransaction.model.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    Recipient findByName(String recipientName);

    void deleteByName(String recipientName);
}