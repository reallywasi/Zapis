package Zapis_Plateform.repository;

import Zapis_Plateform.entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {
    List<StudentDetails> findByUsername(String username);
    boolean existsByCollegeEmail(String collegeEmail); // Check if college email exists
    boolean existsBySap(String sap); // Check if SAP exists
    boolean existsByCollegeEmailAndUsername(String collegeEmail, String username); // Check if college email exists for a specific user
    boolean existsByRegistrationIdAndUsername(String registrationId, String username);
    boolean existsBySapAndUsername(String sap, String username);
    Optional<StudentDetails> findByCollegeEmail(String collegeEmail);
}