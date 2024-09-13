package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("DELETE FROM Image i WHERE i.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Query(value = "INSERT INTO Image (post_id) VALUES (:postId)", nativeQuery = true)
    void insertByPostId(@Param("postId") Long postId);
}
