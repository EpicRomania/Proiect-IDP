package auth.service;

import auth.domain.AccessToken;
import auth.domain.AppUser;
import auth.domain.Role;
import auth.dto.AuthResponse;
import auth.dto.LoginRequest;
import auth.dto.RegisterRequest;
import auth.dto.UserResponse;
import auth.repository.AccessTokenRepository;
import auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
public class AuthService {

    private static final Duration TOKEN_TTL = Duration.ofHours(12);

    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository,
                       AccessTokenRepository accessTokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username is already registered");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email is already registered");
        }

        Role role = request.role() == null ? Role.USER : request.role();
        AppUser user = new AppUser(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                role,
                Instant.now()
        );
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(TOKEN_TTL);
        AccessToken accessToken = accessTokenRepository.save(new AccessToken(createToken(), user, now, expiresAt));
        return new AuthResponse(accessToken.getToken(), expiresAt, UserResponse.from(user));
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        AccessToken accessToken = accessTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid access token"));
        if (accessToken.isExpired(Instant.now())) {
            throw new UnauthorizedException("Access token expired");
        }
        return UserResponse.from(accessToken.getUser());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing bearer token");
        }
        return authorizationHeader.substring("Bearer ".length()).trim();
    }

    private String createToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
