package auth.dto;

import auth.domain.AppUser;
import auth.domain.Role;

import java.time.Instant;

public record UserResponse(Long id, String username, String email, Role role, Instant createdAt) {

    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
