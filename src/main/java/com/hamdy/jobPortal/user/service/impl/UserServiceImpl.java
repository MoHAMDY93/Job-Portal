package com.hamdy.jobPortal.user.service.impl;

import com.hamdy.jobPortal.constants.ApplicationConstants;
import com.hamdy.jobPortal.dto.UserDto;
import com.hamdy.jobPortal.entity.Company;
import com.hamdy.jobPortal.entity.JobPortalUser;
import com.hamdy.jobPortal.entity.Role;
import com.hamdy.jobPortal.repository.CompanyRepository;
import com.hamdy.jobPortal.repository.JobPortalUserRepository;
import com.hamdy.jobPortal.repository.RoleRepository;
import com.hamdy.jobPortal.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {
    private final JobPortalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;

    @Override
    public Optional<UserDto> searchByEmail(String email) {
        return userRepository.findJobPortalUserByEmail(email)
                .map(this::mapToUserDto);
    }

    @Transactional
    @Override
    public UserDto elevateToEmployer(Long userId) {
        JobPortalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
            return mapToUserDto(user);
        }
        if (ApplicationConstants.ROLE_ADMIN.equals(user.getRole().getName())) {
            throw new RuntimeException("Cannot elevate admin user to employer role");
        }

        Role employerRole = roleRepository.findRoleByName(ApplicationConstants.ROLE_EMPLOYER)
                .orElseThrow(() -> new RuntimeException("ROLE_EMPLOYER not found"));
        user.setRole(employerRole);
//        JobPortalUser updatedUser = userRepository.save(user);

        return mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto assignCompanyToEmployer(Long userId, Long companyId) {
        JobPortalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        if (!ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())) {
            throw new RuntimeException("User must be an employer to be assigned to a company");
        }
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));
        user.setCompany(company);

        return mapToUserDto(user);
    }

    private UserDto mapToUserDto(JobPortalUser user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user , userDto);
        userDto.setUserId(user.getId());
        userDto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        userDto.setCompanyId(user.getCompany() != null?  user.getCompany().getId() : null);
        userDto.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);

        return userDto;
    }
}
