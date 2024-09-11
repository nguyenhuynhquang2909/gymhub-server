package com.gymhub.gymhub.service;

import com.gymhub.gymhub.actions.ChangePostStatusAction;
import com.gymhub.gymhub.components.AiHandler;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.helper.PostSequence;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.PostMapper;
import com.gymhub.gymhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private ImageRepository imageRepository;
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
    private PostSequence postSequence;
    @Autowired
    private AiHandler aiHandler;

    @Autowired
    private Cache cache;



    @Autowired
    private TitleService titleService;

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

    @Transactional
    public boolean createPost(PostRequestDTO postRequestDTO, List<MultipartFile> files) {
        try {
            long postId = postSequence.getNextPostId();
            Long ownerId = postRequestDTO.getOwnerId();

            // Validate the member
            Member author = memberRepository.findById(ownerId)
                    .orElseThrow(() -> new IllegalArgumentException("Author not found"));

            // Validate the thread
            Long threadId = postRequestDTO.getThreadId();
            Thread thread = threadRepository.findById(threadId)
                    .orElseThrow(() -> new IllegalArgumentException("Thread not found"));



            //   CallAI PAI
            AiRequestBody aiRequestBody = new AiRequestBody(postRequestDTO.getContent());
            double predictionVal = this.aiHandler.postDataToLocalHost(aiRequestBody);
            ToxicStatusEnum tempToxicEnum;
            boolean tempResolveStatus;
            String tempReason;
            if (predictionVal >= 0.5) {
                tempToxicEnum = ToxicStatusEnum.PENDING;
                tempResolveStatus = true;
                tempReason = "Body Shaming";
            } else {
                tempToxicEnum = ToxicStatusEnum.NOT_TOXIC;
                tempResolveStatus = false;
                tempReason = "";
                // Create the post
            }
            Post post = new Post(postId, LocalDateTime.now(), postRequestDTO.getContent(), author, thread);


            // Handle the encoded image
            List<Image> images = new LinkedList<>();
            for (MultipartFile file : files) {
                Image image = new Image();
                image.setEncodedImage(file.getBytes());
                image.setPost(post);
                images.add(image);
                imageRepository.save(image);
            }

            postRepository.save(post);
            this.inMemoryRepository.addPostToCache(postId, postRequestDTO.getThreadId(), postRequestDTO.getOwnerId(), tempToxicEnum, tempResolveStatus, tempReason);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean updatePost(Long memberId, UpdatePostRequestDTO updatePostRequestDTO, List<MultipartFile> files) {
        try {
            Post post = postRepository.findById(updatePostRequestDTO.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("Post not found"));

            // Validate if the user is authorized to update this post
            if (!post.getAuthor().getId().equals(memberId)) {
                return false;
            }

            // Update post content
            post.setContent(updatePostRequestDTO.getContent());
            if (files == null || files.isEmpty()) {
                imageRepository.deleteByPostId(updatePostRequestDTO.getPostId());
                return true;
            }
            else {
                Set<byte[]> byteArraySet = new HashSet<>();
                for (Image image : post.getImages()) {
                    byteArraySet.add(image.getEncodedImage());
                }
                for (MultipartFile file : files) {
                    int orginalLength = byteArraySet.size();
                    byteArraySet.add(file.getBytes());
                    if (orginalLength != byteArraySet.size()) {
                        Image image = new Image();
                        image.setEncodedImage(file.getBytes());
                        image.setPost(post);
                        imageRepository.save(image);
                        post.getImages().add(image);
                    }
                }
            }
            // Save the updated post
            postRepository.save(post);
            //CALL AI API
            AiRequestBody aiRequestBody = new AiRequestBody(updatePostRequestDTO.getContent());
            double predictionVal = this.aiHandler.postDataToLocalHost(aiRequestBody);
            if (predictionVal >= 0.5){
                //Removing the id of the post from the non_toxic map
                inMemoryRepository.changePostToxicStatusForMemberReporting(updatePostRequestDTO.getPostId(), updatePostRequestDTO.getThreadId(), ToxicStatusEnum.PENDING, "Potentially Body Shaming");
            }
            return true;
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean reportPost(PostRequestDTO postRequestDTO, String reason) {
        long postId = postRequestDTO.getPostId();
        long threadId = postRequestDTO.getThreadId();

        boolean result = inMemoryRepository.changePostToxicStatusForMemberReporting(postId, threadId, ToxicStatusEnum.PENDING, reason);

        ChangePostStatusAction action = new ChangePostStatusAction(
                postId, threadId, ToxicStatusEnum.PENDING, false, reason);
        inMemoryRepository.logAction(action);

        return result;
    }

    public boolean likePost(PostRequestDTO postRequestDTO, MemberRequestDTO memberRequestDTO) {
        long postId = postRequestDTO.getPostId();
        long postOwnerId = postRequestDTO.getOwnerId(); // Post owner's ID
        long memberId = memberRequestDTO.getId(); // Member ID who is liking the post

        // Get the set of posts the member has liked
        Set<Long> likedPosts = cache.getPostLikeListByUser().getOrDefault(memberId, new HashSet<>());

        // Check if the member has already liked the post
        if (likedPosts.contains(postId)) {
            return false; // Already liked, so do nothing
        }

        // Like the post
        likedPosts.add(postId);
        cache.getPostLikeListByUser().put(memberId, likedPosts);

        // Update the like count in the cache
        ConcurrentHashMap<String, Object> postParams = cache.getParametersForAllPosts().get(postId);
        int currentLikeCount = (int) postParams.getOrDefault("LikeCount", 0);
        postParams.put("LikeCount", currentLikeCount + 1);

        // Update the post owner's title based on the new total like count
        Member postOwner = memberRepository.findById(postOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Post owner not found"));
        int totalLikes = inMemoryRepository.getMemberTotalLikeCountByMemberId(postOwnerId);
        // Set the new title based on total likes
        TitleEnum newTitle = titleService.setTitleBasedOnLikeCounts(totalLikes);
        postOwner.setTitle(newTitle.name());
        memberRepository.save(postOwner);

        return true; // Operation succeeded
    }


    public boolean unlikePost(PostRequestDTO postRequestDTO, MemberRequestDTO memberRequestDTO) {
        long postId = postRequestDTO.getPostId();
        long postOwnerId = postRequestDTO.getOwnerId(); // Post owner's ID
        long memberId = memberRequestDTO.getId(); // Member ID who is unliking the post

        // Get the set of posts the member has liked
        Set<Long> likedPosts = cache.getPostLikeListByUser().getOrDefault(memberId, new HashSet<>());

        // Check if the member has not liked the post yet
        if (!likedPosts.contains(postId)) {
            return false; // Not liked, so nothing to undo
        }

        // Unlike the post
        likedPosts.remove(postId);
        cache.getPostLikeListByUser().put(memberId, likedPosts);

        // Update the like count in the cache
        ConcurrentHashMap<String, Object> postParams = cache.getParametersForAllPosts().get(postId);
        int currentLikeCount = (int) postParams.getOrDefault("LikeCount", 0);
        postParams.put("LikeCount", Math.max(0, currentLikeCount - 1)); // Decrease like count, ensuring it doesn't go below 0

        // Update the post owner's title based on the new total like count
        Member postOwner = memberRepository.findById(postOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Post owner not found"));
        int totalLikes = inMemoryRepository.getMemberTotalLikeCountByMemberId(postOwnerId);
        // Set the new title based on total likes
        TitleEnum newTitle = titleService.setTitleBasedOnLikeCounts(totalLikes);
        postOwner.setTitle(newTitle.name());
        memberRepository.save(postOwner);

        return true; // Operation succeeded
    }



}
