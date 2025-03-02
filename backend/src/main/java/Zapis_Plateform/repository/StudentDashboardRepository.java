package Zapis_Plateform.repository;

import Zapis_Plateform.entity.Student_Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentDashboardRepository extends JpaRepository<Student_Dashboard, Long> {
    Optional<Student_Dashboard> findByUsername(String username);
}