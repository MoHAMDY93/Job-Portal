package com.hamdy.jobPortal.repository;

import com.hamdy.jobPortal.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}