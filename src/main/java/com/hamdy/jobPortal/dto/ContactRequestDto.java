package com.hamdy.jobPortal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/**
 * DTO for {@link com.hamdy.jobPortal.entity.Contact}
 */
public record ContactRequestDto(
        @NotBlank(message = "Email can not be empty !!")
        @Email(message = "Invalid email address")
        String email,
        @NotBlank(message = "Message can not be empty !!")
        String message,
        @NotBlank(message = "Name can not be empty !!")
        String name,
        @NotBlank(message = "Subject can not be empty !!")
        String subject,
        @NotBlank(message = "User-Type can not be empty !!")
        @Pattern(regexp = "Job Seeker|Employer|Other" , message = "User type must be one of: Job Seeker, Employer, Other")
        String userType)
        implements Serializable
{

}