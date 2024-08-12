package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.PostRequestDTO;
import com.gymhub.gymhub.dto.PostResponseDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.mapper.PostMapper;
import com.gymhub.gymhub.repository.PostRepository;
import com.gymhub.gymhub.repository.ThreadRepository;

import com.gymhub.gymhub.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private Cache cache;

//    public List<PostResponseDTO> getPostsByThreadId(Long threadId) {
//       List<Post> posts = postRepository.findByThreadId(threadId);
//        return posts.stream()
//                .map(post -> PostMapper.toPostResponseDTO(post, cache, null)) // Replace null with actual userId if needed
//                .collect(Collectors.toList());
//    }
//
//    public List<PostResponseDTO> getPostsByUserId(Long userId) {
//        List<Post> posts = postRepository.findByAuthorId(userId);
//        return posts.stream()
//                .map(post -> PostMapper.toPostResponseDTO(post, cache, userId))
//                .collect(Collectors.toList());
//    }
//
//    public void createPost(Long userId, Long threadId, PostRequestDTO postRequestDTO) {
//        Optional<Member> author = memberRepository.findById(userId);
//        Optional<Thread> thread = threadRepository.findById(threadId);
//
//        if (author.isPresent() && thread.isPresent()) {
//            Post post = PostMapper.toPost(postRequestDTO, author.get(), thread.get());
//            postRepository.save(post);
//        } else {
//            throw new IllegalArgumentException("User or Thread not found");
//        }
//    }
}
