package com.hamdy.jobPortal.company.service.impl;

import com.hamdy.jobPortal.company.service.ICompanyService;
import com.hamdy.jobPortal.constants.ApplicationConstants;
import com.hamdy.jobPortal.dto.CompanyDto;
import com.hamdy.jobPortal.dto.JobDto;
import com.hamdy.jobPortal.entity.Company;
import com.hamdy.jobPortal.repository.CompanyRepository;
import com.hamdy.jobPortal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements ICompanyService {
    private final CompanyRepository companyRepository;

//    @Autowired
//    public CompanyServiceImpl(CompanyRepository companyRepository) {
//        this.companyRepository = companyRepository;
//    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        List<Company> companiesList = companyRepository.fetchCompaniesWithJobsByStatus(ApplicationConstants.ACTIVE_STATUS);
        return companiesList.stream().map(this::transformCompanyToDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public boolean createCompany(CompanyDto companyDto) {
        Company company = transformCompanyDtoToEntity(companyDto);
        Company savedCompany = companyRepository.save(company);
        return savedCompany.getId() != null && savedCompany.getId() > 0;
    }

    @Cacheable("companies")
    @Override
    public List<CompanyDto> getAllCompaniesForAdmin() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream().map(this::transformCompanyToDtoForAdmin).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public boolean updateCompanyDetails(Long id, CompanyDto companyDto) {
        int updatedRecords = companyRepository.updateCompanyDetails(
                id, companyDto.name(), companyDto.logo(),
                companyDto.industry(), companyDto.size(), companyDto.rating(),
                companyDto.locations(), companyDto.founded(), companyDto.description(),
                companyDto.employees(), companyDto.website()
        );
        return updatedRecords > 0;
    }

    @Transactional
    @Override
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }

    private CompanyDto transformCompanyToDto(Company company) {
        List<JobDto> jobDtos = company.getJobs().stream()
                .map(ApplicationUtility::transformJobToDto)
                .collect(Collectors.toList());

        return new CompanyDto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt(),jobDtos);
    }

    private Company transformCompanyDtoToEntity(CompanyDto companyDto) {
        Company company = new Company();
        BeanUtils.copyProperties(companyDto , company);
        return company;
    }

    private CompanyDto transformCompanyToDtoForAdmin(Company company) {
        return new CompanyDto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt(), null);
    }
}
