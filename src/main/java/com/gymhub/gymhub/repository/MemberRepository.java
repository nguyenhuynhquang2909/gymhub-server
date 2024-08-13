package com.gymhub.gymhub.repository;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByUsername(String userName);
    Boolean checkIfMemberExistsByUserName(String userName);
    Boolean checkIfMemberExistsByEmail(String email);
}

