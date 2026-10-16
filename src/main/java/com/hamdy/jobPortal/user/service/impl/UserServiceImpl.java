package com.hamdy.jobPortal.user.service.impl;

import com.hamdy.jobPortal.constants.ApplicationConstants;
import com.hamdy.jobPortal.dto.*;
import com.hamdy.jobPortal.entity.*;
import com.hamdy.jobPortal.repository.*;
import com.hamdy.jobPortal.user.service.IUserService;
import com.hamdy.jobPortal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {
    private final JobPortalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

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

    @Transactional
    @Override
    public ProfileDto createOrUpdateProfile(String userEmail, String profileJson, MultipartFile profilePicture, MultipartFile resume) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // Parse JsonString to ProfileDto
        ProfileDto profileDto = objectMapper.readValue(profileJson, ProfileDto.class);
        Profile savedProfile = profileRepository.save(mapToProfile(profile, profileDto, profilePicture, resume));
        return mapToProfileDto(savedProfile, false);
    }

    @Override
    public ProfileDto getProfile(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() ->  new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), false);
    }

    @Override
    public ProfileDto getProfilePicture(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), true);
    }

    @Override
    public ProfileDto getResume(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (user.getProfile() == null) {
            return null;
        }
        return mapToProfileDto(user.getProfile(), true);
    }

    @Override
    @Transactional
    public JobDto saveJob(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        user.getSavedJobs().add(job);
        return ApplicationUtility.transformJobToDto(job);
    }

    @Override
    @Transactional
    public void unsaveJob(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        user.getSavedJobs().remove(job);
    }

    @Override
    public List<JobDto> getSavedJobs(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return user.getSavedJobs().stream().map(ApplicationUtility::transformJobToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobApplicationDto applyForJob(String userEmail, ApplyJobRequestDto applyJobRequestDto) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Long jobId = applyJobRequestDto.jobId();
        if (jobApplicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You have already applied for this job before");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);
        application.setAppliedAt(Instant.now());
        application.setStatus(ApplicationConstants.PENDING);
        application.setCoverLetter(applyJobRequestDto.coverLetter());
        JobApplication savedApplication = jobApplicationRepository.save(application);
        job.setApplicationsCount(job.getApplicationsCount() != null ? job.getApplicationsCount() + 1 : 1);
        return mapToJobApplicationDto(savedApplication);
    }

    @Override
    @Transactional
    public void withdrawApplication(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        if (!jobApplicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You have not applied for this job");
        }
        jobApplicationRepository.deleteByUserIdAndJobId(user.getId(), jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        if (job.getApplicationsCount() != null && job.getApplicationsCount() > 0) {
            job.setApplicationsCount(job.getApplicationsCount() - 1);
        }
    }

    @Override
    public List<JobApplicationDto> getJobSeekerApplications(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        return user.getJobApplications().stream().map(this::mapToJobApplicationDto).collect(Collectors.toList());
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

    private Profile mapToProfile(Profile profile, ProfileDto profileDto, MultipartFile profilePicture, MultipartFile resume) {
        profile.setJobTitle(profileDto.jobTitle());
        profile.setLocation(profileDto.location());
        profile.setExperienceLevel(profileDto.experienceLevel());
        profile.setProfessionalBio(profileDto.professionalBio());
        profile.setPortfolioWebsite(profileDto.portfolioWebsite());

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                profile.setProfilePicture(profilePicture.getBytes());
                profile.setProfilePictureName(profilePicture.getOriginalFilename());
                profile.setProfilePictureType(profilePicture.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to set profile picture", e);
            }
        }

        if (resume != null && !resume.isEmpty()) {
            try {
                profile.setResume(resume.getBytes());
                profile.setResumeName(resume.getOriginalFilename());
                profile.setResumeType(resume.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to set resume", e);
            }
        }

        return profile;
    }

    private ProfileDto mapToProfileDto(Profile profile, boolean includeBinaryData) {
        ProfileDto profileDto;
        if (includeBinaryData) {
            profileDto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), profile.getProfilePicture(),
                    profile.getProfilePictureName(), profile.getProfilePictureType(), profile.getResume(),
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt()
            );
        } else {
            profileDto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), null,
                    profile.getProfilePictureName(), profile.getProfilePictureType(), null,
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt());
        }

        return profileDto;
    }

    private JobApplicationDto mapToJobApplicationDto(JobApplication application) {
        // Map profile if exists
        ProfileDto profileDto = null;
        Profile profile = application.getUser().getProfile();
        if (profile != null) {
            profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUser().getId(),
                    profile.getJobTitle(),
                    profile.getLocation(),
                    profile.getExperienceLevel(),
                    profile.getProfessionalBio(),
                    profile.getPortfolioWebsite(),
                    profile.getProfilePicture(),
                    profile.getProfilePictureName(),
                    profile.getProfilePictureType(),
                    profile.getResume(),
                    profile.getResumeName(),
                    profile.getResumeType(),
                    profile.getCreatedAt(),
                    profile.getUpdatedAt()
            );
        }
        return new JobApplicationDto(
                application.getId(),
                application.getUser().getId(),
                application.getUser().getName(),
                application.getUser().getEmail(),
                application.getUser().getMobileNumber(),
                profileDto,
                ApplicationUtility.transformJobToDto(application.getJob()),
                application.getAppliedAt(),
                application.getStatus(),
                application.getCoverLetter(),
                application.getNotes()
        );
    }
}
