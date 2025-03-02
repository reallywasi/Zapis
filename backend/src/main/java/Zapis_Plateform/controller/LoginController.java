package Zapis_Plateform.controller;

import Zapis_Plateform.dto.AuthRequest;
import Zapis_Plateform.entity.APO_Dashboard;
import Zapis_Plateform.entity.User;
import Zapis_Plateform.repository.APORepository;
import Zapis_Plateform.service.UserService;
import Zapis_Plateform.utils.SessionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import Zapis_Plateform.service.AdminService;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final SessionManager sessionManager;
    private final APORepository apoRepository;

    public LoginController(UserService userService, PasswordEncoder passwordEncoder, AdminService adminService, SessionManager sessionManager, APORepository apoRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
        this.sessionManager = sessionManager;
        this.apoRepository = apoRepository;
    }

    @PostMapping("/apo")
    public ResponseEntity<?> loginAPO(@RequestBody AuthRequest authRequest) {
        Optional<User> userOptional = userService.findByUsername(authRequest.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the user is approved
            if (!user.getApproved()) {
                return ResponseEntity.status(403).body("User not approved by admin!");
            }

            // Validate password
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Invalid credentials!");
            }

            // Removed industry validation
            Optional<APO_Dashboard> apoDashboardOptional = apoRepository.findByUsername(user.getUsername());
            APO_Dashboard apoDashboard;

            if (apoDashboardOptional.isEmpty()) {
                // Create a new APO_Dashboard entry if it doesn't exist, without industry
                apoDashboard = APO_Dashboard.builder()
                        .username(user.getUsername())
                        .data("Initial data") // Default data
                        .user(user) // Link to User entity
                        .build();
                apoRepository.save(apoDashboard);
            } else {
                apoDashboard = apoDashboardOptional.get();
            }

            // Track APO login
            sessionManager.loginAPO(user.getUsername());
            return ResponseEntity.ok("APO login successful! Redirect to APO Dashboard.");
        }
        return ResponseEntity.status(404).body("User not found!");
    }

    @PostMapping("/apo/logout")
    public ResponseEntity<?> logoutAPO(@RequestBody AuthRequest authRequest) {
        if (!sessionManager.isAPOLoggedIn(authRequest.getUsername())) {
            return ResponseEntity.badRequest().body("APO is not logged in!");
        }
        sessionManager.logoutAPO(authRequest.getUsername());
        return ResponseEntity.ok("APO logged out successfully!");
    }

    @PostMapping("/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AuthRequest authRequest) {
        adminService.registerAdmin(); // Ensure admin exists in DB
        if (adminService.authenticateAdmin(authRequest.getUsername(), authRequest.getPassword())) {
            sessionManager.loginAdmin();
            return ResponseEntity.ok("Admin login successful!");
        } else {
            return ResponseEntity.status(401).body("Invalid admin credentials!");
        }
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<?> logoutAdmin() {
        if (!sessionManager.logoutAdmin()) {
            return ResponseEntity.badRequest().body("Admin is already logged out!");
        }
        return ResponseEntity.ok("Admin logged out successfully!");
    }
}