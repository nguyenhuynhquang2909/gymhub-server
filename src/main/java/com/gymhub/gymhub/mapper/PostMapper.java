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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

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
        if (post.getImages() != null) {
            List<String> encodedImages = new LinkedList<>();
            for (Image image: post.getImages()) {
                String base64Image = Base64.getEncoder().encodeToString(image.getEncodedImage()); // Convert byte[] to Base64 string
                encodedImages.add(base64Image);
            }

            dto.setEncodedImage(encodedImages); // Set the Base64 string in the response DTO
        }

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



