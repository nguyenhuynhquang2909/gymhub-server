package com.gymhub.gymhub.service;

import com.gymhub.gymhub.actions.ChangePostStatusAction;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.PostMapper;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.PostRepository;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private UserRepository userRepository;
    @Autowired
    private InMemoryRepository inMemoryRepository;

    @Autowired
    private Cache cache;
    private long actionIdCounter = 0;

    public List<PostResponseDTO> getPostsByThreadId(Long threadId) {
        List<Post> posts = postRepository.findByThreadId(threadId);
        return posts.stream()
                .map(post -> PostMapper.toPostResponseDTO(post, cache, null)) // Replace null with actual userId if needed
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .map(post -> PostMapper.toPostResponseDTO(post, cache, userId))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Void> createPost(Long userId, Long threadId, PostRequestDTO postRequestDTO) {
        Optional<Member> author = userRepository.findById(userId);
        Optional<Thread> thread = threadRepository.findById(threadId);

        if (author.isPresent() && thread.isPresent()) {
            Post post = PostMapper.toPost(postRequestDTO, author.get(), thread.get());
            postRepository.save(post);
            return new ResponseEntity<>(HttpStatus.CREATED); // Return CREATED status
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return NOT FOUND if user or thread is missing
        }
    }

    public ResponseEntity<Void> updatePost(UpdatePostContentDTO updatePostContentDTO) {
        Optional<Member> member = userRepository.findById(updatePostContentDTO.getAuthorId());
        Optional<Thread> thread = threadRepository.findById(updatePostContentDTO.getThreadId());
        Optional<Post> post = postRepository.findById(updatePostContentDTO.getPostId());

        if (member.isPresent() && thread.isPresent() && post.isPresent()) {
            Post existingPost = post.get();

            // Check if the member is the author of the post
            if (existingPost.getAuthor().getId().equals(member.get().getId())) {
                // Update the post content
                existingPost.setContent(updatePostContentDTO.getContent());

                // Update the post image
                Image updatedImage = existingPost.getImage();
                if (updatedImage == null) {
                    updatedImage = new Image(String.valueOf(updatePostContentDTO.getEncodedImage()));
                    updatedImage.setPost(existingPost);
                    existingPost.setImage(updatedImage);
                } else {
                    updatedImage.setEncodedImage(String.valueOf(updatePostContentDTO.getEncodedImage()));
                }

                // Save the updated post
                postRepository.save(existingPost);

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public boolean reportPost(ReportPostRequestDTO reportPostRequestDTO, long threadId) {
        // Extract necessary information from the DTO
        long postId = reportPostRequestDTO.getId();
        int from = reportPostRequestDTO.getFrom();
        int to = reportPostRequestDTO.getTo();
        String reason = reportPostRequestDTO.getReason();

        // Call the inMemoryRepository's changePostStatus method with the extracted values
        boolean result = inMemoryRepository.changePostStatus(postId, threadId, from, to, reason);

        // Log the action using the constructor that requires threadId
        ChangePostStatusAction action = new ChangePostStatusAction(
                ++actionIdCounter, postId, threadId, from, to, reason);
        inMemoryRepository.logAction(action);

        return result;
    }



}
