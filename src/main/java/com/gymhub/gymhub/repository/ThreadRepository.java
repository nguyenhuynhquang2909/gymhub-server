package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByCategory(ThreadCategoryEnum category);

    List<Thread> findByOwnerId(Long ownerId);

    @Override
    @EntityGraph(value = "Thread.owner", type = EntityGraph.EntityGraphType.LOAD)
    List<Thread> findAll();


    @Query("SELECT t FROM Thread t JOIN FETCH t.owner WHERE t.id IN :ids")
    List<Thread> findAllByIdsWithOwner(@Param("ids") List<Long> ids);

    @EntityGraph(value = "Thread.full", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT t FROM Thread t WHERE t.id IN :ids")
    List<Thread> findAllThreadsByThreadIdList(@Param("ids") List<Long> ids);


}

