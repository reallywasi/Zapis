package Zapis_Plateform.controller;

import Zapis_Plateform.dto.RegisterRequest;
import Zapis_Plateform.entity.User;
import Zapis_Plateform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@ModelAttribute RegisterRequest request) {
        try {
            @SuppressWarnings("unused")
            User savedUser = userService.registerUser(request);
            return ResponseEntity.ok("User registered successfully! Pending approval.");
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}