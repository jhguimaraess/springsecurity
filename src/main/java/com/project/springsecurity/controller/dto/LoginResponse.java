package com.project.springsecurity.controller.dto;

public record LoginResponse(String accsessToken, Long expiresIn ) {
}
