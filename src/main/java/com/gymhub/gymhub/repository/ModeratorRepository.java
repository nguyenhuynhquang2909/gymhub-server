package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Long> {
    Optional<Moderator> findModByUsername(String userName);
    Boolean checkIfModExistsByUserName(String userName);
    Boolean checkIfModExistsByEmail(String email);
}
