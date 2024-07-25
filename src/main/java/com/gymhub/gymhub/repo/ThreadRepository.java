package com.gymhub.gymhub.repo;

import com.gymhub.gymhub.domain.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, String> {
}
