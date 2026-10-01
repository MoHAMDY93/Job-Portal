package com.hamdy.jobPortal.auth;

import com.hamdy.jobPortal.constants.ApplicationConstants;
import com.hamdy.jobPortal.dto.LoginRequestDto;
import com.hamdy.jobPortal.dto.LoginResponseDto;
import com.hamdy.jobPortal.dto.RegisterRequestDto;
import com.hamdy.jobPortal.dto.UserDto;
import com.hamdy.jobPortal.entity.JobPortalUser;
import com.hamdy.jobPortal.entity.Role;
import com.hamdy.jobPortal.repository.JobPortalUserRepository;
import com.hamdy.jobPortal.repository.RoleRepository;
import com.hamdy.jobPortal.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JobPortalUserRepository jobPortalUserRepository;
    private final RoleRepository roleRepository;
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    @PostMapping(value = "/login/public" , version = "1.0")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            var resultAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password()));
            // Done: Generate JWT Token
            String jwtToken = jwtUtil.generateJwtToken(resultAuthentication);
            var userDto = new UserDto();
            var loggedUser = (JobPortalUser) resultAuthentication.getPrincipal();
            BeanUtils.copyProperties(loggedUser , userDto);
            userDto.setRole(loggedUser.getRole().getName());
            userDto.setUserId(loggedUser.getId());
            return ResponseEntity.ok().body(new LoginResponseDto(
                    HttpStatus.OK.getReasonPhrase(),
                    userDto,
                    jwtToken
            ));
        } catch (AuthenticationException authenticationException) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED , "Authentication Failed");
        } catch (Exception exception) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR , "An unexpected error occurred");
        }
    }

    @PostMapping(value = "/register/public" , version = "1.0")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        JobPortalUser jobPortalUser = new JobPortalUser();
        BeanUtils.copyProperties(registerRequestDto , jobPortalUser);
        jobPortalUser.setPasswordHash(passwordEncoder.encode(registerRequestDto.password()));
        Role role = roleRepository.findRoleByName(ApplicationConstants.ROLE_JOB_SEEKER)
                .orElseThrow(()->new IllegalArgumentException("Role not found: " +
                        ApplicationConstants.ROLE_JOB_SEEKER));
        jobPortalUser.setRole(role);
        jobPortalUserRepository.save(jobPortalUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }


    private ResponseEntity<LoginResponseDto> buildErrorResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(new LoginResponseDto(message , null , null));
    }
}
