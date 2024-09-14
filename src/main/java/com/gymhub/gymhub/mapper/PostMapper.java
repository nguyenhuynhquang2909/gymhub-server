package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.dto.PendingPostDTO;
import com.gymhub.gymhub.dto.PostResponseDTO;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import com.gymhub.gymhub.repository.InMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Component
public class PostMapper {

    @Autowired
    private InMemoryRepository inMemoryRepository;

    // Convert Post entity to PostResponseDTO
    public PostResponseDTO postToPostResponseDTO(Post post) {
        if (post == null) {
            return null; // Ensure null safety
        }

        PostResponseDTO dto = new PostResponseDTO();

        // Set basic post information
        dto.setId(post.getId());
        dto.setCreationDateTime(post.getCreationDateTime());
        dto.setContent(post.getContent());

        // Set author information
        dto.setAuthorName(post.getAuthor().getUserName());
        dto.setAuthorId(post.getAuthor().getId().toString());

        // Fetch and set the like and view counts from the in-memory repository
        Integer likeCount = inMemoryRepository.getPostLikeCountByPostId(post.getId());
        Integer viewCount = inMemoryRepository.getPostViewCountByPostId(post.getId());

        dto.setLikeCount(likeCount != null ? likeCount : 0);
        dto.setViewCount(viewCount != null ? viewCount : 0);

        // Fetch and set post's resolve status and toxic status
        dto.setResolveStatus(inMemoryRepository.getResolveStatusByPostId(post.getId()));
        ToxicStatusEnum toxicStatus = inMemoryRepository.getToxicStatusByPostId(post.getId());
        dto.setToxicStatus(toxicStatus != null ? toxicStatus : ToxicStatusEnum.NOT_TOXIC);

        // Check if the post has been liked by the current member
        dto.setBeenLiked(inMemoryRepository.checkIfAPostHasBeenLikedByAMemberId(post.getId(), post.getAuthor().getId()));

        // Fetch and set the reason for toxic status (if any)
        dto.setReason(inMemoryRepository.getReasonByPostId(post.getId()));

        // Set the post count of the related thread
        dto.setPostCount(inMemoryRepository.getPostCountOfAThreadByThreadId(post.getThread().getId()));

        // Encode images in Base64 format for the frontend
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            List<String> encodedImages = new LinkedList<>();
            for (Image image : post.getImages()) {
                String base64Image = Base64.getEncoder().encodeToString(image.getEncodedImage()); // Convert byte[] to Base64
                encodedImages.add(base64Image);
            }
            dto.setEncodedImage(encodedImages);
        }

        // Additional member info fields
        dto.setUsername(post.getAuthor().getUserName());
        dto.setTitle(post.getAuthor().getTitle() != null ? post.getAuthor().getTitle() : null);
        // Check if the avatar is not null before encoding it
        if (post.getAuthor().getAvatar() != null) {
            dto.setMemberAvatar(Base64.getEncoder().encodeToString(post.getAuthor().getAvatar())); // Convert avatar byte[] to Base64 string
        } else {
            dto.setMemberAvatar(null); // Handle null avatar
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
        Long threadId = post.getThread().getId();

        return new PendingPostDTO(postID, authorUsername, content, reason,threadId);
    }
}
