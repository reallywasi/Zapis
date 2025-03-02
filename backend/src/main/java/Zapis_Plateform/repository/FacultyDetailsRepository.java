package Zapis_Plateform.repository;

import Zapis_Plateform.entity.FacultyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyDetailsRepository extends JpaRepository<FacultyDetails, Long> {
    List<FacultyDetails> findByUsername(String username);
    boolean existsByCollegeEmail(String collegeEmail);
    boolean existsBySap(String sap);
    boolean existsByRegistrationIdAndUsername(String registrationId, String username);
    Optional<FacultyDetails> findByRegistrationIdAndUsername(String registrationId, String username);
    Optional<FacultyDetails> findByCollegeEmail(String collegeEmail);
    Optional<FacultyDetails> findByName(String name); // Added method
}