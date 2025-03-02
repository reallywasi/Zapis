package Zapis_Plateform.service;

import Zapis_Plateform.entity.Admin;
import Zapis_Plateform.entity.User;
import Zapis_Plateform.repository.AdminRepository;
import Zapis_Plateform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_USERNAME = "zapis.services@gmail.com";
    private static final String ADMIN_PASSWORD = "Zapis@4173";

    public AdminService(AdminRepository adminRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isAdminRegistered() {
        return adminRepository.findByUsername(ADMIN_USERNAME).isPresent();
    }

    public void registerAdmin() {
        if (!isAdminRegistered()) {
            Admin admin = Admin.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .build();

            adminRepository.save(admin);
        }
    }

    public boolean authenticateAdmin(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        return admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword());
    }

    // Get all unapproved users
    public List<User> getUnapprovedUsers() {
        return userRepository.findByApproved(false); // Assuming 'approved' is a boolean field
    }

    // Get all approved users
    public List<User> getApprovedUsers() {
        return userRepository.findByApproved(true);
    }

    // Get all users (both approved and unapproved)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Approve a user
    public String approveUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setApproved(true);  // Assuming there's an 'approved' field
            userRepository.save(user);
            return "User approved successfully.";
        } else {
            return "User not found.";
        }
    }

    // Reject a user
    public String rejectUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setApproved(false);
            userRepository.save(user);
            return "User rejected successfully.";
        } else {
            return "User not found.";
        }
    }

    public String unapproveUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setApproved(false);  // Set approved to false to unapprove
            userRepository.save(user);
            return "User unapproved successfully.";
        } else {
            return "User not found.";
        }
    }
}