package auth;

import auth.domain.AccessToken;
import auth.domain.AppUser;
import auth.domain.Role;
import auth.dto.LoginRequest;
import auth.dto.RegisterRequest;
import auth.repository.AccessTokenRepository;
import auth.repository.UserRepository;
import auth.service.AuthService;
import auth.service.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final AccessTokenRepository accessTokenRepository = mock(AccessTokenRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthService authService = new AuthService(userRepository, accessTokenRepository, passwordEncoder);

    @Test
    void registerCreatesUserWithDefaultRole() {
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = authService.register(new RegisterRequest(
                "andrei",
                "andrei@example.com",
                "secret123",
                null
        ));

        assertThat(response.username()).isEqualTo("andrei");
        assertThat(response.role()).isEqualTo(Role.USER);
    }

    @Test
    void loginRejectsInvalidPassword() {
        AppUser user = new AppUser(
                "andrei",
                "andrei@example.com",
                passwordEncoder.encode("correct-password"),
                Role.USER,
                Instant.now()
        );
        when(userRepository.findByUsername("andrei")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("andrei", "wrong-password")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void loginReturnsAccessToken() {
        AppUser user = new AppUser(
                "andrei",
                "andrei@example.com",
                passwordEncoder.encode("correct-password"),
                Role.ORGANIZER,
                Instant.now()
        );
        when(userRepository.findByUsername("andrei")).thenReturn(Optional.of(user));
        when(accessTokenRepository.save(any(AccessToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = authService.login(new LoginRequest("andrei", "correct-password"));

        assertThat(response.token()).isNotBlank();
        assertThat(response.user().role()).isEqualTo(Role.ORGANIZER);
    }
}
