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
    private MemberRepository memberRepository;
    @Autowired
    private InMemoryRepository inMemoryRepository;

    @Autowired
    private Cache cache;
    private long actionIdCounter = 0;

    public List<PostResponseDTO> getPostsByThreadId(Long threadId) {
        List<Post> posts = postRepository.findByThreadId(threadId);
        return posts.stream()
                .map(post -> PostMapper.postToPostResponseDTO(post)) // Replace null with actual userId if needed
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .map(post -> PostMapper.postToPostResponseDTO(post))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Void> createPost(PostRequestDTO postRequestDTO) {
        Optional<Member> author = memberRepository.findById(postRequestDTO.getAuthorId());
        Optional<Thread> thread = threadRepository.findById(postRequestDTO.getThreadId());
        if (author.isPresent() && thread.isPresent()) {

            long id = HelperMethod.generateUniqueIds();
            System.out.println("Generated ID: " + id); //need tools for unique thread id
            postRequestDTO.setPostId(id);
            Post post = PostMapper.postRequestToPost(postRequestDTO, author.get(), thread.get());
            //ADD POST TO CACHE
            ToxicStatusEnum tempToxicEnum = ToxicStatusEnum.NOT_TOXIC;  //temporary set ToxicStatus = NOT-TOXIC
            boolean tempResolveStatus = false;
            String tempReason = "";
            //Then Call the AI here to generate the toxicStatus

            inMemoryRepository.addPostToCache(postRequestDTO.getPostId(),postRequestDTO.getThreadId(), postRequestDTO.getAuthorId(), tempToxicEnum, tempResolveStatus, tempReason );
            postRepository.save(post);
            return new ResponseEntity<>(HttpStatus.CREATED); // Return CREATED status
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return NOT FOUND if user or thread is missing
        }
    }

    public ResponseEntity<Void> updatePost(UpdatePostContentDTO updatePostContentDTO) {
        Optional<Member> member = memberRepository.findById(updatePostContentDTO.getAuthorId());
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
    public boolean reportPost(PostRequestDTO postRequestDTO, String reason) {
        // Extract necessary information from the DTO
        long postId = postRequestDTO.getPostId();
        long threadId = postRequestDTO.getThreadId();
        //reason already inside para
        ToxicStatusEnum toxicStatusEnum = ToxicStatusEnum.PENDING;
        boolean resolveStatus = false;

        // Call the inMemoryRepository's changePostStatus method with the extracted values
        boolean result = inMemoryRepository.changePostToxicStatusForMemberReporting(postId, threadId, reason);

        // Log the action using the constructor that requires threadId
        ChangePostStatusAction action = new ChangePostStatusAction(
                ++actionIdCounter,"changePostToxicStatusForMemberReporting", postId, threadId,toxicStatusEnum, resolveStatus, reason);
        inMemoryRepository.logAction(action);

        return result;
    }



}
