package com.gymhub.gymhub.service;

import com.gymhub.gymhub.actions.ChangePostStatusAction;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.helper.HelperMethod;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.PostMapper;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.PostRepository;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InMemoryRepository inMemoryRepository;
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private Cache cache;

    private long actionIdCounter = 0;

    public List<PostResponseDTO> getPostsByThreadId(Long threadId) {
        List<Post> posts = postRepository.findByThreadId(threadId);
        return posts.stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .map(postMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    public boolean createPost(Long memberID, PostRequestDTO postRequestDTO) {
        try {
            long id = HelperMethod.generateUniqueIds();
            postRequestDTO.setPostId(id);

            Member author = memberRepository.findById(memberID)
                    .orElseThrow(() -> new IllegalArgumentException("Author not found"));
            Thread thread = threadRepository.findById(postRequestDTO.getThreadId())
                    .orElseThrow(() -> new IllegalArgumentException("Thread not found"));

            Post post = postMapper.postRequestToPost(postRequestDTO, author, thread);

            // Temporary setup for the post before AI analysis
            ToxicStatusEnum tempToxicEnum = ToxicStatusEnum.NOT_TOXIC;
            boolean tempResolveStatus = false;
            String tempReason = "";

            // Add post to cache
            inMemoryRepository.addPostToCache(postRequestDTO.getPostId(), postRequestDTO.getThreadId(), memberID, tempToxicEnum, tempResolveStatus, tempReason);

            postRepository.save(post);
            return true; // Operation succeeded
        } catch (Exception e) {
            return false; // Operation failed due to exception
        }
    }


    public boolean updatePost(Long memberId, UpdatePostContentDTO updatePostContentDTO) {
        try {
            Post post = postRepository.findById(updatePostContentDTO.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("Post not found"));

            if (!post.getAuthor().getId().equals(memberId)) {
                return false; // User is not authorized to update this post
            }
            //CALL AI API
            post.setContent(updatePostContentDTO.getContent());

            Image updatedImage = post.getImage();
            if (updatedImage == null) {
                updatedImage = new Image(String.valueOf(updatePostContentDTO.getEncodedImage()));
                updatedImage.setPost(post);
                post.setImage(updatedImage);
            } else {
                updatedImage.setEncodedImage(String.valueOf(updatePostContentDTO.getEncodedImage()));
            }

            postRepository.save(post);
            return true; // Operation succeeded
        } catch (IllegalArgumentException | SecurityException e) {
            return false; // Operation failed due to exception
        }
    }


    public boolean reportPost(PostRequestDTO postRequestDTO, String reason) {
        long postId = postRequestDTO.getPostId();
        long threadId = postRequestDTO.getThreadId();

        boolean result = inMemoryRepository.changePostToxicStatusForMemberReporting(postId, threadId, reason);

        ChangePostStatusAction action = new ChangePostStatusAction(
                ++actionIdCounter, "changePostToxicStatusForMemberReporting", postId, threadId, ToxicStatusEnum.PENDING, false, reason);
        inMemoryRepository.logAction(action);

        return result;
    }
}
