package com.hanwha.tax.batch.tax.repository;

import com.hanwha.tax.batch.entity.Tax2;
import com.hanwha.tax.batch.tax.model.TaxId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tax2Repository extends JpaRepository<Tax2, TaxId> {
}
