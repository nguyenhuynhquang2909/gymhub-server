package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Thread;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
//    Thread findThreadById(Long id);

//    @Query
//    Thread findByThreadIdList(List<Long> threadIdList);
//    @Query("SELECT t FROM Thread t WHERE t.id IN :id")
@EntityGraph(value = "Thread.owner", type = EntityGraph.EntityGraphType.LOAD)
    List<Thread> findByIdIn(List<Long> ids);

    @Query("SELECT t FROM Thread t JOIN FETCH t.owner WHERE t.id IN :ids")
    List<Thread> findAllByIdsWithOwner(@Param("ids") List<Long> ids);


}

