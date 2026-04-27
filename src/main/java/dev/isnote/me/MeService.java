package dev.isnote.me;

import dev.isnote.auth.AuthService;
import dev.isnote.auth.AuthTokenResponseDTO;
import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import dev.isnote.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Transactional
public class MeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public MeService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public MeProfileResponseDTO getProfile(User authenticatedUser) {
        User user = loadActiveUser(authenticatedUser);
        return mapProfile(user);
    }

    public MeProfileResponseDTO updateProfile(User authenticatedUser, UpdateProfileDTO body) {
        User user = loadActiveUser(authenticatedUser);
        String normalizedEmail = normalizeEmail(body.email());

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, user.getId())) {
            throw new BusinessException("Email address already exists.", HttpStatus.CONFLICT);
        }

        user.setName(body.name().trim());
        user.setEmail(normalizedEmail);

        User savedUser = userRepository.save(user);
        return mapProfile(savedUser);
    }

    public AuthTokenResponseDTO changePassword(User authenticatedUser, ChangePasswordDTO body) {
        User user = loadActiveUser(authenticatedUser);

        if (!passwordEncoder.matches(body.currentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect.", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(body.newPassword(), user.getPassword())) {
            throw new BusinessException("New password must be different from the current password.", HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(body.newPassword()));
        User savedUser = userRepository.save(user);

        authService.revokeActiveSessions(savedUser.getId());
        return authService.issueTokensFor(savedUser);
    }

    private User loadActiveUser(User authenticatedUser) {
        return userRepository.findById(authenticatedUser.getId())
            .filter(User::isEnabled)
            .orElseThrow(() -> new BusinessException("Authenticated user was not found.", HttpStatus.UNAUTHORIZED));
    }

    private MeProfileResponseDTO mapProfile(User user) {
        return new MeProfileResponseDTO(
            user.getName(),
            user.getEmail(),
            user.getAvatarStorageKey() == null ? "" : user.getAvatarStorageKey()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
