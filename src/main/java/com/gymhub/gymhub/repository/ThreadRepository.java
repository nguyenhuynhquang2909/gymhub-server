package com.gymhub.gymhub.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import com.gymhub.gymhub.domain.Thread;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByCategory(String category);

    List<Thread> findByOwnerId(Long ownerId);

    @EntityGraph(value = "Thread.owner", type = EntityGraph.EntityGraphType.LOAD)
    List<Thread> findAll();

    //more method

//    @Query
//    List<Thread> findByListOfThreadIds(List<Long> threadIds);





//    List<Thread> findByThreadIds(List<Long> threadIds);


    List<Thread> findByIdIn(List<Long> ids);

}

