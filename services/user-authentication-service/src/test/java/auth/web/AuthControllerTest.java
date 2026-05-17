package auth.web;

import auth.domain.Role;
import auth.dto.AuthResponse;
import auth.dto.LoginRequest;
import auth.dto.RegisterRequest;
import auth.dto.UserResponse;
import auth.service.AuthService;
import auth.service.ConflictException;
import auth.service.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(ApiExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void registerReturnsCreatedUser() throws Exception {
        RegisterRequest request = new RegisterRequest("andrei", "andrei@example.com", "secret123", Role.ORGANIZER);
        when(authService.register(request)).thenReturn(new UserResponse(1L, "andrei", "andrei@example.com", Role.ORGANIZER, Instant.parse("2026-05-16T10:00:00Z")));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("andrei"))
                .andExpect(jsonPath("$.role").value("ORGANIZER"));
    }

    @Test
    void loginReturnsAccessToken() throws Exception {
        LoginRequest request = new LoginRequest("andrei", "secret123");
        when(authService.login(request)).thenReturn(new AuthResponse(
                "token-value",
                Instant.parse("2026-05-16T22:00:00Z"),
                new UserResponse(1L, "andrei", "andrei@example.com", Role.USER, Instant.parse("2026-05-16T10:00:00Z"))
        ));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-value"))
                .andExpect(jsonPath("$.user.username").value("andrei"));
    }

    @Test
    void meUsesBearerAuthorizationHeader() throws Exception {
        when(authService.currentUser("Bearer token-value"))
                .thenReturn(new UserResponse(1L, "andrei", "andrei@example.com", Role.USER, Instant.parse("2026-05-16T10:00:00Z")));

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer token-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("andrei"));
    }

    @Test
    void usersReturnsAllUsers() throws Exception {
        when(authService.listUsers()).thenReturn(List.of(
                new UserResponse(1L, "andrei", "andrei@example.com", Role.USER, Instant.parse("2026-05-16T10:00:00Z")),
                new UserResponse(2L, "maria", "maria@example.com", Role.ORGANIZER, Instant.parse("2026-05-16T11:00:00Z"))
        ));

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].role").value("ORGANIZER"));
    }

    @Test
    void invalidRegisterPayloadReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("", "not-an-email", "short", Role.USER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void duplicateRegisterReturnsConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("andrei", "andrei@example.com", "secret123", Role.USER);
        when(authService.register(request)).thenThrow(new ConflictException("Username is already registered"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void invalidLoginReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("andrei", "wrong-password");
        when(authService.login(request)).thenThrow(new UnauthorizedException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
