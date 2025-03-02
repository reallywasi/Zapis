package Zapis_Plateform.repository;

import Zapis_Plateform.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    List<StudentAttendance> findByStudentUsername(String studentUsername);
    Optional<StudentAttendance> findByStudentUsernameAndClassCodeAndDateAndStartTimeEndTime(
            String studentUsername, String classCode, String date, String startTimeEndTime);
}