package com.gymhub.gymhub.repository;
import com.gymhub.gymhub.domain.ForumAccount;
import com.gymhub.gymhub.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserName(String userName);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);
}

