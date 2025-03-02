package Zapis_Plateform.repository;

import Zapis_Plateform.entity.RICDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RICDetailsRepository extends JpaRepository<RICDetails, Long> {
    List<RICDetails> findByUsername(String username);
    boolean existsByRoomNoAndUsername(String roomNo, String username); // Check if roomNo exists for a specific user
    boolean existsByIpAddressAndUsername(String ipAddress, String username); // Check if ipAddress exists for a specific user
    boolean existsByClassCodeAndUsername(String classCode, String username); // Check if classCode exists for a specific user
}