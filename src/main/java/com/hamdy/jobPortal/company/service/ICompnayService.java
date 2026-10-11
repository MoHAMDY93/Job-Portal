package com.hamdy.jobPortal.company.service;

import com.hamdy.jobPortal.dto.CompanyDto;

import java.util.List;

public interface ICompnayService {
    List<CompanyDto> getAllCompanies();

    boolean createCompany(CompanyDto companyDto);

    List<CompanyDto> getAllCompaniesForAdmin();

    boolean updateCompanyDetails(Long id, CompanyDto companyDto);

    void deleteCompanyById(Long id);
}
