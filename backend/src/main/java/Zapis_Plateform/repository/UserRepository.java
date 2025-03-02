package Zapis_Plateform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Zapis_Plateform.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByApproved(Boolean approved);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
}
