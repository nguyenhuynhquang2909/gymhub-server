package com.gymhub.gymhub.mapper;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.PostRequestDTO;
import com.gymhub.gymhub.dto.PostResponseDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.domain.Member;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostResponseDTO toPostResponseDTO(Post post, Cache cache, Long userId) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setCreationDateTime(post.getCreationDateTime());
        dto.setLikeCount(cache.getPostLikeCountByPostId(post.getId()));
        dto.setViewCount(cache.getPostViewCountByPostId(post.getId()));
        dto.setBeenReport(cache.checkIfAPostHasBeenReported(post.getId()));
        dto.setBeenLiked(cache.checkIfAPostHasBeenLikedByAMemberId(post.getId(), userId));
        dto.setPostCount(cache.getPostCountOfAThreadByThreadId(post.getThread().getId()));
        dto.setAuthorName(post.getAuthor().getUserName());
        dto.setAuthorId(post.getAuthor().getId().toString());
        dto.setAuthorAvatar(post.getAuthor().getStringAvatar());
        dto.setName(post.getContent());
        return dto;
    }

    public static Post toPost(PostRequestDTO postRequestDTO, Member author, Thread thread) {
        return new Post(

                LocalDateTime.now(),
                postRequestDTO.getContent(),
                postRequestDTO.getEncodedImages().stream()
                        .map(encodedImage -> new Image(encodedImage.getBytes()))
                        .collect(Collectors.toList())
        );
    }
    public static PostRequestDTO toPostRequestDTO(Post post) {
        PostRequestDTO dto = new PostRequestDTO();
        dto.setAuthorId(post.getAuthor().getId());
        dto.setContent(post.getContent());
        dto.setEncodedImages(post.getImages().stream()
                .map(Image::getEncodedImage)
                .collect(Collectors.toList()));
        dto.setThreadId(post.getThread().getId().toString());
        return dto;
    }


}
