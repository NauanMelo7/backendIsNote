package dev.isnote.me;

import dev.isnote.auth.AuthService;
import dev.isnote.auth.AuthTokenResponseDTO;
import dev.isnote.auth.AuthenticatedUserDTO;
import dev.isnote.contentkey.ContentKeyService;
import dev.isnote.exception.BusinessException;
import dev.isnote.user.RoleUser;
import dev.isnote.user.User;
import dev.isnote.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @Mock
    private ContentKeyService contentKeyService;

    @InjectMocks
    private MeService meService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Nauan", "nauan@isnote.app", "encoded-password", RoleUser.USER);
        user.setId(UUID.randomUUID());
    }

    @Test
    void shouldReturnCurrentProfile() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        MeProfileResponseDTO response = meService.getProfile(user);

        assertThat(response.name()).isEqualTo("Nauan");
        assertThat(response.email()).isEqualTo("nauan@isnote.app");
    }

    @Test
    void shouldUpdateProfile() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("new@isnote.app", user.getId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MeProfileResponseDTO response = meService.updateProfile(
            user,
            new UpdateProfileDTO("Novo Nome", "NEW@isnote.app")
        );

        assertThat(response.name()).isEqualTo("Novo Nome");
        assertThat(response.email()).isEqualTo("new@isnote.app");
    }

    @Test
    void shouldRejectDuplicateEmailOnProfileUpdate() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("ja-existe@isnote.app", user.getId())).thenReturn(true);

        assertThatThrownBy(() -> meService.updateProfile(user, new UpdateProfileDTO("Novo", "ja-existe@isnote.app")))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Email address already exists.");
    }

    @Test
    void shouldChangePasswordAndReissueTokens() {
        AuthTokenResponseDTO tokens = new AuthTokenResponseDTO(
            "access",
            Instant.parse("2026-04-24T10:00:00Z"),
            "refresh",
            Instant.parse("2026-05-24T10:00:00Z"),
            new AuthenticatedUserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().name())
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(contentKeyService.existsForUser(any(User.class))).thenReturn(false);
        when(passwordEncoder.matches("old-password", "encoded-password")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "encoded-password")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authService.issueTokensFor(user)).thenReturn(tokens);

        AuthTokenResponseDTO response = meService.changePassword(
            user,
            new ChangePasswordDTO("old-password", "new-password", null)
        );

        verify(authService).revokeActiveSessions(user.getId());
        verify(authService).issueTokensFor(user);
        assertThat(response.accessToken()).isEqualTo("access");
    }

    @Test
    void shouldRejectWrongCurrentPassword() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(contentKeyService.existsForUser(any(User.class))).thenReturn(false);
        when(passwordEncoder.matches(anyString(), eq("encoded-password"))).thenReturn(false);

        assertThatThrownBy(() -> meService.changePassword(user, new ChangePasswordDTO("wrong", "new-password", null)))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Current password is incorrect.");
    }
}
