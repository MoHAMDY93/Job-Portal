package com.hamdy.jobPortal.repository;

import com.hamdy.jobPortal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    void deleteByUserIdAndJobId(Long userId, Long jobId);

    List<JobApplication> findByUserIdOrderByAppliedAtDesc(Long userId);
}