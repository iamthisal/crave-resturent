package com.crave.service;

import com.crave.config.JwtService;
import com.crave.dto.*;
import com.crave.model.User;
import com.crave.repository.UserRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder passwords;
  private final JwtService jwt;

  public AuthResponse register(RegisterRequest request) {
    String email = request.email().trim().toLowerCase(Locale.ROOT);
    if (users.existsByEmail(email))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
    User user = new User();
    user.setName(request.name().trim());
    user.setEmail(email);
    user.setPassword(passwords.encode(request.password()));
    return response(users.save(user));
  }

  public AuthResponse login(LoginRequest request) {
    User user =
        users
            .findByEmail(request.email().trim().toLowerCase(Locale.ROOT))
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    if (!passwords.matches(request.password(), user.getPassword()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    return response(user);
  }

  private AuthResponse response(User user) {
    return new AuthResponse(
        jwt.createToken(user),
        new AuthResponse.UserInfo(user.getId(), user.getName(), user.getEmail(), user.getRole()));
  }
}
