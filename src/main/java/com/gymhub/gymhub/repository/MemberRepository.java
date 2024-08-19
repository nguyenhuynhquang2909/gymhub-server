package com.gymhub.gymhub.repository;
import com.gymhub.gymhub.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByUserName(String userName);
    // Keep the name of these methods to follow Spring Data JPA conventions
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);
}

