package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(value = "Post.author", type = EntityGraph.EntityGraphType.LOAD)
    List<Post> findAll();

    List<Post> findByThreadId(Long threadId);

    List<Post> findByAuthorId(Long authorId);




}

