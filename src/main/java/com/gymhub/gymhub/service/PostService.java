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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    private Cache cache;

    private long actionIdCounter = 0;

    public List<PostResponseDTO> getPostsByThreadId(Long threadId) {
        List<Post> posts = postRepository.findByThreadId(threadId);
        return posts.stream()
                .map(PostMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .map(PostMapper::postToPostResponseDTO)
                .collect(Collectors.toList());
    }

    public void createPost(PostRequestDTO postRequestDTO) {
        long id = HelperMethod.generateUniqueIds();
        postRequestDTO.setPostId(id);

        Member author = memberRepository.findById(postRequestDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        Thread thread = threadRepository.findById(postRequestDTO.getThreadId())
                .orElseThrow(() -> new IllegalArgumentException("Thread not found"));

        Post post = PostMapper.postRequestToPost(postRequestDTO, author, thread);

        // Temporary setup for the post before AI analysis
        ToxicStatusEnum tempToxicEnum = ToxicStatusEnum.NOT_TOXIC;
        boolean tempResolveStatus = false;
        String tempReason = "";

        // Add post to cache
        inMemoryRepository.addPostToCache(postRequestDTO.getPostId(), postRequestDTO.getThreadId(), postRequestDTO.getAuthorId(), tempToxicEnum, tempResolveStatus, tempReason);

        postRepository.save(post);
    }

    public void updatePost(UpdatePostContentDTO updatePostContentDTO) {
        Post post = postRepository.findById(updatePostContentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getId().equals(updatePostContentDTO.getAuthorId())) {
            throw new SecurityException("You do not have permission to update this post");
        }

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
