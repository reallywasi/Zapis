package Zapis_Plateform.controller;

import Zapis_Plateform.dto.StudentDashboardResponse;
import Zapis_Plateform.entity.Student_Dashboard;
import Zapis_Plateform.service.StudentDashboardService;
import Zapis_Plateform.utils.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@RestController
@RequestMapping("/api/student/dashboard")
public class StudentDashboardController {

    @Autowired
    private StudentDashboardService studentDashboardService;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/save")
    public ResponseEntity<?> saveDashboardData(@RequestParam String username, @RequestBody String data) {
        if (!sessionManager.isStudentLoggedIn(username)) {
            return ResponseEntity.status(401).body("Student not logged in!");
        }
        Student_Dashboard savedData = studentDashboardService.saveDashboardData(username, data);
        return ResponseEntity.ok(savedData);
    }

    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData(HttpServletRequest request) {
        String username = sessionManager.getLoggedInStudent();
        if (username == null || !sessionManager.isStudentLoggedIn(username)) {
            return ResponseEntity.status(401).body("Student not logged in!");
        }
        StudentDashboardResponse dashboardData = studentDashboardService.getStudentDashboardData(username);
        String ipAddress = getLocalIpAddress();
        dashboardData.setIpAddress(ipAddress != null ? ipAddress : getClientIp(request));
        return ResponseEntity.ok(dashboardData);
    }

    @PostMapping("/markAttendance")
    public ResponseEntity<?> markAttendance(HttpServletRequest request) {
        String username = sessionManager.getLoggedInStudent();
        if (username == null || !sessionManager.isStudentLoggedIn(username)) {
            return ResponseEntity.status(401).body("Student not logged in!");
        }
        String ipAddress = getLocalIpAddress();
        if (ipAddress == null) {
            return ResponseEntity.badRequest().body("Unable to determine IP address!");
        }
        try {
            String result = studentDashboardService.markAttendance(username, ipAddress);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getAttendanceSummary")
    public ResponseEntity<?> getAttendanceSummary() {
        String username = sessionManager.getLoggedInStudent();
        if (username == null || !sessionManager.isStudentLoggedIn(username)) {
            return ResponseEntity.status(401).body("Student not logged in!");
        }
        return ResponseEntity.ok(studentDashboardService.getAttendanceSummary(username));
    }

    @GetMapping("/getFullTimetable")
    public ResponseEntity<?> getFullTimetable() {
        String username = sessionManager.getLoggedInStudent();
        if (username == null || !sessionManager.isStudentLoggedIn(username)) {
            return ResponseEntity.status(401).body("Student not logged in!");
        }
        return ResponseEntity.ok(studentDashboardService.getFullTimetable(username));
    }

    @PostMapping("/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String loggedInStudent = sessionManager.getLoggedInStudent();
        if (loggedInStudent == null || !sessionManager.isStudentLoggedIn(loggedInStudent)) {
            return ResponseEntity.status(401).body("Student not logged in!");
        }
        try {
            String message = studentDashboardService.uploadProfileImage(loggedInStudent, file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload profile image: " + e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}