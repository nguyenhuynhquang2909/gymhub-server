package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.PendingPostDTO;
import com.gymhub.gymhub.dto.PostRequestDTO;
import com.gymhub.gymhub.dto.PostResponseDTO;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.repository.InMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class PostMapper {
    @Autowired
    private InMemoryRepository inMemoryRepository;

    // Convert Post entity to PostResponseDTO
    public PostResponseDTO postToPostResponseDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();

        // Set basic fields
        dto.setId(post.getId());
        dto.setCreationDateTime(post.getCreationDateTime());

        // Set counts from cache
        Integer likeCount = inMemoryRepository.getPostLikeCountByPostId(post.getId());
        Integer viewCount = inMemoryRepository.getPostViewCountByPostId(post.getId());

        dto.setLikeCount(likeCount != null ? likeCount : 0);
        dto.setViewCount(viewCount != null ? viewCount : 0);

        // Set status fields from cache
        dto.setResolveStatus(inMemoryRepository.getResolveStatusByPostId(post.getId()));
        ToxicStatusEnum toxicStatus = inMemoryRepository.getToxicStatusByPostId(post.getId());
        dto.setToxicStatus(toxicStatus != null ? toxicStatus : ToxicStatusEnum.NOT_TOXIC);

        dto.setBeenLiked(inMemoryRepository.checkIfAPostHasBeenLikedByAMemberId(post.getId(), post.getAuthor().getId()));
        dto.setReason(inMemoryRepository.getReasonByPostId(post.getId()));

        // Set additional post-related fields
        dto.setPostCount(inMemoryRepository.getPostCountOfAThreadByThreadId(post.getThread().getId()));

        // Set author information
        dto.setAuthorName(post.getAuthor().getUserName());
        dto.setAuthorId(post.getAuthor().getId().toString());

        // Set content and image fields
        dto.setName(post.getContent());

        // Convert byte[] to Base64 string for the frontend
        if (post.getImage() != null && post.getImage().getEncodedImage() != null) {
            dto.setEncodedImage(Base64.getEncoder().encodeToString(post.getImage().getEncodedImage()).getBytes()); // Convert byte[] to Base64 string
        }

        return dto;
    }

    // Convert PostRequestDTO to Post entity
    public Post postRequestToPost(PostRequestDTO postRequestDTO, Member author, Thread thread) {
        Image image = null;

        if (postRequestDTO.getEncodedImage() != null && !postRequestDTO.getEncodedImage().isEmpty()) {
            byte[] decodedImage = Base64.getDecoder().decode(postRequestDTO.getEncodedImage()); // Decode Base64 to byte[]
            image = new Image(decodedImage);
        }

        Post post = new Post(
                LocalDateTime.now(),
                postRequestDTO.getContent(),
                image,
                author,
                thread
        );

        if (image != null) {
            image.setPost(post); // Set the post reference in the image
        }

        return post;
    }

    // Convert Post entity to PostRequestDTO
    public PostRequestDTO postToPostRequestDTO(Post post) {
        PostRequestDTO dto = new PostRequestDTO();
        dto.setPostId(post.getId()); // Set the post ID
        dto.setContent(post.getContent()); // Set the content
        // Convert byte[] to Base64 string if image exists
        dto.setEncodedImage(post.getImage() != null ? Base64.getEncoder().encodeToString(post.getImage().getEncodedImage()) : null);
        dto.setThreadId(post.getThread().getId()); // Set the thread ID
        return dto;
    }

    // Convert Post entity to PendingPostDTO (for internal purposes)
    public PendingPostDTO postToPendingPostDTO(Post post) {
        if (post == null) {
            return null;
        }

        Long postID = post.getId();
        String authorUsername = post.getAuthor() != null ? post.getAuthor().getUserName() : null;
        String content = post.getContent();
        String reason = inMemoryRepository.getReasonByPostId(postID);

        return new PendingPostDTO(postID, authorUsername, content, reason);
    }
}
