package Zapis_Plateform.repository;

import Zapis_Plateform.entity.APO_Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface APORepository extends JpaRepository<APO_Dashboard, Long> {
    Optional<APO_Dashboard> findByUsername(String username); // Fetch APO_Dashboard by username
}