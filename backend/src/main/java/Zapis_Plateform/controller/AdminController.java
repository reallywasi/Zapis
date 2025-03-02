package Zapis_Plateform.controller;

import Zapis_Plateform.entity.User;
import Zapis_Plateform.service.AdminService;
import Zapis_Plateform.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private SessionManager sessionManager;

    private void checkAdminLogin() {
        if (!sessionManager.isAdminLoggedIn()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin must log in first!");
        }
    }

    @GetMapping("/unapproved")
    public List<User> getUnapprovedUsers() {
        checkAdminLogin();
        return adminService.getUnapprovedUsers();
    }

    @GetMapping("/approved")
    public List<User> getApprovedUsers() {
        checkAdminLogin();
        return adminService.getApprovedUsers();
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        checkAdminLogin();
        return adminService.getAllUsers();
    }

    @PutMapping("/approve/{userId}")
    public String approveUser(@PathVariable Long userId) {
        checkAdminLogin();
        return adminService.approveUser(userId);
    }

    @PutMapping("/reject/{userId}")
    public String rejectUser(@PathVariable Long userId) {
        checkAdminLogin();
        return adminService.rejectUser(userId);
    }

    @PutMapping("/unapprove/{userId}")
    public String unapproveUser(@PathVariable Long userId) {
        checkAdminLogin();
        return adminService.unapproveUser(userId);
    }
}