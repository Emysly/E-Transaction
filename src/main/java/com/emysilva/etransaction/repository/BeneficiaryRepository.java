package com.emysilva.etransaction.repository;

import com.emysilva.etransaction.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Beneficiary findByName(String beneficiaryName);

    void deleteByName(String beneficiaryName);
}