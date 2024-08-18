package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.PendingPostDTO;
import com.gymhub.gymhub.dto.PostRequestDTO;
import com.gymhub.gymhub.dto.PostResponseDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.repository.InMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class PostMapper {
  @Autowired
    private static InMemoryRepository inMemoryRepository;





    public static PostResponseDTO postToPostResponseDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();

        // Set basic fields
        dto.setId(post.getId());
        dto.setCreationDateTime(post.getCreationDateTime());

        // Set counts from cache (assuming cache methods are available)
        dto.setLikeCount(inMemoryRepository.getPostLikeCountByPostId(post.getId()));
        dto.setViewCount(inMemoryRepository.getPostViewCountByPostId(post.getId()));

        // Set status fields from cache
        dto.setResolveStatus(inMemoryRepository.getResolveStatusByPostId(post.getId()));
        dto.setToxicStatus(inMemoryRepository.getToxicStatusByPostId(post.getId()));
        dto.setBeenLiked(inMemoryRepository.checkIfAPostHasBeenLikedByAMemberId(post.getId(), post.getAuthor().getId()));
        dto.setReason(inMemoryRepository.getReasonByPostId(post.getId()));
        // Set additional post-related fields
        dto.setPostCount(inMemoryRepository.getPostCountOfAThreadByThreadId(post.getThread().getId()));

        // Set author information
        dto.setAuthorName(post.getAuthor().getUserName());
        dto.setAuthorId(post.getAuthor().getId().toString());
        dto.setAuthorAvatar(post.getAuthor().getStringAvatar());

        // Set content and image fields
        dto.setName(post.getContent());
        dto.setEncodedImage(post.getImage() != null ? post.getImage().getEncodedImage() : null); // Handle possible null image

        return dto;
    }

    public static Post postRequestToPost(PostRequestDTO postRequestDTO, Member author, Thread thread) {
        Image image = new Image(postRequestDTO. getEncodedImage().getBytes());
        Post post = new Post(
                LocalDateTime.now(),
                postRequestDTO.getContent(),
                image,
                author,
                thread
        );
        image.setPost(post); // Set the post reference in the image
        return post;
    }

    public static PostRequestDTO postToPostRequestDTO(Post post) {
        PostRequestDTO dto = new PostRequestDTO();
        dto.setPostId(post.getId()); // Set the post ID
        dto.setAuthorId(post.getAuthor().getId()); // Set the author ID
        dto.setContent(post.getContent()); // Set the content
        dto.setEncodedImage(post.getImage() != null ? post.getImage().getEncodedImage() : null); // Set the encoded image if it exists
        dto.setThreadId(post.getThread().getId()); // Set the thread ID
        return dto;
    }

    public static PendingPostDTO postToPendingPostDTO(Post post) {
        if (post == null) {
            return null;
        }

        Long postID = post.getId();
        String authorUsername = post.getAuthor() != null ? post.getAuthor().getUserName() : null;
        String content = post.getContent();

        return new PendingPostDTO(postID, authorUsername, content);
    }


}
