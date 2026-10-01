package com.hamdy.jobPortal.dto;

public record LoginResponseDto(String message , UserDto user , String jwtToken) {
}
