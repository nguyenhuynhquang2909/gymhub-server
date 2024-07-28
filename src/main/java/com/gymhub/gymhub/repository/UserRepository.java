package com.gymhub.gymhub.repository;
import com.gymhub.gymhub.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<Member, Long> {
}

