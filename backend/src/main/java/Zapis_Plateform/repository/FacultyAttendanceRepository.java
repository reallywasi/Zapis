package Zapis_Plateform.repository;

import Zapis_Plateform.entity.FacultyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyAttendanceRepository extends JpaRepository<FacultyAttendance, Long> {
    List<FacultyAttendance> findByFacultyUsername(String facultyUsername);
    Optional<FacultyAttendance> findByFacultyUsernameAndClassCodeAndDateAndStartTimeEndTime(
            String facultyUsername, String classCode, String date, String startTimeEndTime);
}