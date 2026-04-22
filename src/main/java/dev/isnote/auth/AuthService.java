package dev.isnote.auth;

import dev.isnote.exception.BusinessException;
import dev.isnote.user.User;
import dev.isnote.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO createUser(AuthRegisterDTO body) {
        if(userRepository.existsByEmail(body.email())) {
            throw new BusinessException("Email address already exists.", HttpStatus.CONFLICT);
        }

        User newUser = this.userRepository.save(new
            User(body.name(),
            body.email(),
            passwordEncoder.encode(body.password())));


        return responseUserDTO(newUser);
    }

    public AuthResponseDTO responseUserDTO(User data) {
        return new AuthResponseDTO(
            data.getDisplayName(),
            data.getEmail()
        );
    }
}
