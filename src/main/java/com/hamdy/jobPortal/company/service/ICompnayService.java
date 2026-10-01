package com.hamdy.jobPortal.company.service;

import com.hamdy.jobPortal.dto.CompanyDto;
import com.hamdy.jobPortal.entity.Company;

import java.util.List;

public interface ICompnayService {
    List<CompanyDto> getAllCompanies();
}
