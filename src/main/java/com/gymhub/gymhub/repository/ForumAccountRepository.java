package com.gymhub.gymhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gymhub.gymhub.domain.ForumAccount;

@Repository
public interface ForumAccountRepository extends JpaRepository<ForumAccount, Long> {
    Optional<ForumAccount> findByUserName(String userName);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);
}
