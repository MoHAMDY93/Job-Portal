package com.hamdy.jobPortal.repository;

import com.hamdy.jobPortal.entity.JobPortalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPortalUserRepository extends JpaRepository<JobPortalUser, Long> {
    Optional<JobPortalUser> readUserByEmailOrMobileNumber(String email, String mobileNumber);

    Optional<JobPortalUser> findJobPortalUserByEmail(String email);
}