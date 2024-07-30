package com.gymhub.gymhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(Member user);
}