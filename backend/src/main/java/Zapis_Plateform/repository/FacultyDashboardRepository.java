package Zapis_Plateform.repository;

import Zapis_Plateform.entity.Faculty_Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyDashboardRepository extends JpaRepository<Faculty_Dashboard, Long> {
    Optional<Faculty_Dashboard> findByUsername(String username);
}