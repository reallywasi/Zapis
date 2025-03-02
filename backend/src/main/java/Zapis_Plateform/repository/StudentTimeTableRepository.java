package Zapis_Plateform.repository;

import Zapis_Plateform.entity.StudentTimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentTimeTableRepository extends JpaRepository<StudentTimeTable, Long> {
    List<StudentTimeTable> findByUsername(String username);
    List<StudentTimeTable> findByCourseAndBatchAndUsername(String course, String batch, String username);
    List<StudentTimeTable> findByCourseAndSpecialisationAndBatchAndUsername(String course, String specialisation, String batch, String username);
    void deleteByUsername(String username);
    List<StudentTimeTable> findBySemesterAndUsername(String semester, String username); // Existing method
}