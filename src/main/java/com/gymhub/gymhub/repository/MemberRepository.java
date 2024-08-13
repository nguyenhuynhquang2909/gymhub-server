package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
