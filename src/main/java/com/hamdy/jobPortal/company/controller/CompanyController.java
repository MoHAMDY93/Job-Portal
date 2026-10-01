package com.hamdy.jobPortal.company.controller;

import com.hamdy.jobPortal.dto.CompanyDto;
import com.hamdy.jobPortal.company.service.ICompnayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final ICompnayService companyService;

//    @Autowired // optional
//    public CompanyController(ICompnayService companyService) {
//        this.companyService = companyService;
//    }

    @GetMapping(path= "/public" , version = "1.0")
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        List<CompanyDto> companiesList = companyService.getAllCompanies();
        return ResponseEntity.ok().body(companiesList);
    }
    
}
