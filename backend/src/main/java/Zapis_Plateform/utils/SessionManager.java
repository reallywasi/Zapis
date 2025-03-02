package Zapis_Plateform.utils;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SessionManager {
    private boolean isAdminLoggedIn = false;
    private Set<String> loggedInAPOs = new HashSet<>();
    private Set<String> loggedInFaculties = new HashSet<>();
    private Set<String> loggedInStudents = new HashSet<>();

    // Admin session management
    public boolean isAdminLoggedIn() {
        return isAdminLoggedIn;
    }

    public void loginAdmin() {
        if (isAdminLoggedIn) {
            throw new IllegalStateException("Admin already logged in!");
        }
        isAdminLoggedIn = true;
    }

    public boolean logoutAdmin() {
        if (!isAdminLoggedIn) {
            return false;
        }
        isAdminLoggedIn = false;
        return true;
    }

    // APO session management
    public boolean isAPOLoggedIn(String username) {
        return loggedInAPOs.contains(username);
    }

    public void loginAPO(String username) {
        if (loggedInAPOs.contains(username)) {
            throw new IllegalStateException("APO already logged in!");
        }
        loggedInAPOs.add(username);
    }

    public boolean logoutAPO(String username) {
        if (!loggedInAPOs.contains(username)) {
            return false;
        }
        loggedInAPOs.remove(username);
        return true;
    }

    public String getLoggedInAPO() {
        return loggedInAPOs.isEmpty() ? null : loggedInAPOs.iterator().next();
    }

    // Faculty session management
    public boolean isFacultyLoggedIn(String username) {
        return loggedInFaculties.contains(username);
    }

    public void loginFaculty(String username) {
        if (loggedInFaculties.contains(username)) {
            throw new IllegalStateException("Faculty already logged in!");
        }
        loggedInFaculties.add(username);
    }

    public boolean logoutFaculty(String username) {
        if (!loggedInFaculties.contains(username)) {
            return false;
        }
        loggedInFaculties.remove(username);
        return true;
    }

    public String getLoggedInFaculty() {
        return loggedInFaculties.isEmpty() ? null : loggedInFaculties.iterator().next();
    }

    // Student session management
    public boolean isStudentLoggedIn(String username) {
        return loggedInStudents.contains(username);
    }

    public void loginStudent(String username) {
        if (loggedInStudents.contains(username)) {
            throw new IllegalStateException("Student already logged in!");
        }
        loggedInStudents.add(username);
    }

    public boolean logoutStudent(String username) {
        if (!loggedInStudents.contains(username)) {
            return false;
        }
        loggedInStudents.remove(username);
        return true;
    }

    public String getLoggedInStudent() {
        return loggedInStudents.isEmpty() ? null : loggedInStudents.iterator().next();
    }

    public void clearAllSessions() {
        loggedInAPOs.clear();
        loggedInFaculties.clear();
        loggedInStudents.clear();
        isAdminLoggedIn = false;
    }
}