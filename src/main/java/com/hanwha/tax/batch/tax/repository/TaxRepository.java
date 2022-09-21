package com.hanwha.tax.batch.tax.repository;

import com.hanwha.tax.batch.entity.Tax;
import com.hanwha.tax.batch.tax.model.TaxId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax, TaxId> {
}
