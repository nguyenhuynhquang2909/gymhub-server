package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Long> {
    // Adjusted the method name to match the field name exactly
    Optional<Moderator> findModByUserName(String userName);

    // Keep the names of these methods consistent with the field names in the Moderator entity
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);


}
