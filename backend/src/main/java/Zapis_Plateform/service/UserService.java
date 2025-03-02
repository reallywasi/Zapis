package Zapis_Plateform.service;

import Zapis_Plateform.dto.RegisterRequest;
import Zapis_Plateform.entity.User;
import Zapis_Plateform.repository.UserRepository;
import Zapis_Plateform.utils.FileUploadUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }    

    public User registerUser(RegisterRequest request) throws IOException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken!");
        }

        String logoPath = FileUploadUtil.saveFile(request.getLogo(), request.getUsername());

        User user = User.builder()
                .typeOfIndustry(request.getTypeOfIndustry())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .organizationName(request.getOrganizationName())
                .organizationCode(request.getOrganizationCode())
                .logoPath(logoPath)
                .approved(false)
                .build();

        return userRepository.save(user);
    }
}