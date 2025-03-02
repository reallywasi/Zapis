package Zapis_Plateform.repository;

import Zapis_Plateform.entity.FacultyTimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyTimeTableRepository extends JpaRepository<FacultyTimeTable, Long> {
    List<FacultyTimeTable> findByUsername(String username);
    List<FacultyTimeTable> findByRegistrationIdAndUsername(String registrationId, String username);
    void deleteByRegistrationIdAndUsername(String registrationId, String username);
    List<FacultyTimeTable> findByRegistrationIdAndDate(String registrationId, String date);
    List<FacultyTimeTable> findByRegistrationId(String registrationId);
    List<FacultyTimeTable> findBySemesterAndUsername(String semester, String username); // Existing method
}