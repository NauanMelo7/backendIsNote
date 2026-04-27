package dev.isnote.user;

import dev.isnote.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserResponseDTO getUserDetail(UUID id) {

        User findUser = this.userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        return new UserResponseDTO(
            findUser.getName(),
            findUser.getEmail(),
            findUser.getAvatarStorageKey()
        );
    }
}
