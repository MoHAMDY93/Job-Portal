package com.hamdy.jobPortal.repository;

import com.hamdy.jobPortal.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // it is totally optional, coz just extending the JpaRepository interface is enough fpr Spring to add this interface into the context
public interface CompanyRepository extends JpaRepository<Company , Long> {

}
