package com.hamdy.jobPortal.user.service;

import com.hamdy.jobPortal.dto.UserDto;

import java.util.Optional;

public interface IUserService {
    Optional<UserDto> searchByEmail(String email);
    UserDto elevateToEmployer(Long userId);
    UserDto assignCompanyToEmployer(Long userId, Long companyId);
}
