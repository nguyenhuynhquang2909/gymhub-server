package com.gymhub.gymhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteByUser(@Param("user") Member user);
}