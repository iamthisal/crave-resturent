package com.crave.dto;


import com.crave.model.Role;
public record AuthResponse(String token, UserInfo user) {
    public record UserInfo(Long id, String name, String email, Role role) { }
}
