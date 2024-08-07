package com.gymhub.gymhub.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import com.gymhub.gymhub.domain.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByCategory(String category);

    List<Thread> findByOwnerId(Long ownerId);
}

