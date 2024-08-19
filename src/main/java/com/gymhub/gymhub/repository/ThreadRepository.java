package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Thread;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByCategory(String category);

    List<Thread> findByOwnerId(Long ownerId);

    @Override
    @EntityGraph(value = "Thread.owner", type = EntityGraph.EntityGraphType.LOAD)
    List<Thread> findAll();

    //more method
    Thread findThreadById(Long id);

}

