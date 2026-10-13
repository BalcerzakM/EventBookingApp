package web.eventbookingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.eventbookingapp.dto.RegistrationDto;
import web.eventbookingapp.exception.EmailAlreadyExistsException;
import web.eventbookingapp.exception.UsernameAlreadyExistsException;
import web.eventbookingapp.model.UserEntity;
import web.eventbookingapp.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegistrationDto user) throws UsernameAlreadyExistsException {
        if(userRepository.existsByUsername((user.getUsername()))){
            throw new UsernameAlreadyExistsException("Username is already in use!");
        }
        if (userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistsException("Email is already in use!");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setEmail(user.getEmail());
        userEntity.setRole("USER");
        userRepository.save(userEntity);
    }
}
