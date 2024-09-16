package com.gymhub.gymhub.service;

import com.gymhub.gymhub.actions.ChangePostStatusAction;
import com.gymhub.gymhub.components.AiHandler;
import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.domain.Image;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.helper.PostSequence;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.PostMapper;
import com.gymhub.gymhub.repository.*;
import com.gymhub.gymhub.service.CustomException.UnauthorizedUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ToxicStatusEnum createPost(PostRequestDTO postRequestDTO, List<MultipartFile> files, UserDetails user) throws IOException {
        long postId = postSequence.getNextPostId();
        System.out.println(postId);
        Long ownerId = ((CustomUserDetails)user).getId();

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
        System.out.println(predictionVal);
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

        postRepository.save(post);

        // Handle the encoded image
        if (files != null){
            List<Image> images = new LinkedList<>();
            for (MultipartFile file : files) {
                Image image = new Image();
                image.setPost(post);
                image.setEncodedImage(file.getBytes());
                images.add(image);
                imageRepository.save(image);
            }
        }

        this.inMemoryRepository.addPostToCache(postRequestDTO.getThreadId(), postId, ownerId, tempToxicEnum, tempResolveStatus, tempReason);
        return tempToxicEnum;
    }

    @Transactional
    public ToxicStatusEnum updatePost(Long userId, UpdatePostRequestDTO updatePostRequestDTO, List<MultipartFile> files) throws IOException, UnauthorizedUserException {
        Post post = postRepository.findById(updatePostRequestDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (post.getAuthor().getId() != userId) {
            throw new UnauthorizedUserException("You are not allowed to update this post");
        }
        // Update post content
        post.setContent(updatePostRequestDTO.getContent());
        if (files == null || files.isEmpty()) {
            imageRepository.deleteByPostId(updatePostRequestDTO.getPostId());
        }
        else {
            Set<byte[]> byteArraySet = new HashSet<>();
            for (Image image : post.getImages()) {
                byteArraySet.add(image.getEncodedImage());
            }
            if (files != null){
                for (MultipartFile file : files) {
                    int originalLength = byteArraySet.size();
                    byteArraySet.add(file.getBytes());
                    if (originalLength != byteArraySet.size()) {
                        Image image = new Image();
                        image.setEncodedImage(file.getBytes());
                        image.setPost(post);
                        imageRepository.save(image);
                        post.getImages().add(image);
                    }
                }
            }

        }
        // Save the updated post
        postRepository.save(post);
        //CALL AI API
        AiRequestBody aiRequestBody = new AiRequestBody(updatePostRequestDTO.getContent());
        double predictionVal = this.aiHandler.postDataToLocalHost(aiRequestBody);
        ToxicStatusEnum currentToxicStatusEnum = ToxicStatusEnum.NOT_TOXIC;
        if (predictionVal >= 0.5){
            currentToxicStatusEnum = ToxicStatusEnum.PENDING;
            //Removing the id of the post from the non_toxic map
            inMemoryRepository.changePostToxicStatusForMemberReporting(updatePostRequestDTO.getPostId(), updatePostRequestDTO.getThreadId(), currentToxicStatusEnum, "Potentially Body Shaming");
        }
        return currentToxicStatusEnum;
    }

    public boolean reportPost(long threadId, String reason, Long postId) {
        boolean result = inMemoryRepository.changePostToxicStatusForMemberReporting(postId, threadId, ToxicStatusEnum.PENDING, reason);

        ChangePostStatusAction action = new ChangePostStatusAction(
                postId, threadId, ToxicStatusEnum.PENDING, false, reason);
        inMemoryRepository.logAction(action);

        return result;
    }

    public boolean likePost(Long memberId, Long postId, Long postOwnerId) {



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
        postOwner.setTitle(newTitle);
        memberRepository.save(postOwner);

        return true; // Operation succeeded
    }


    public boolean unlikePost(Long memberId, Long postId, Long postOwnerId) {

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
        postOwner.setTitle(newTitle);
        memberRepository.save(postOwner);

        return true; // Operation succeeded
    }



}
