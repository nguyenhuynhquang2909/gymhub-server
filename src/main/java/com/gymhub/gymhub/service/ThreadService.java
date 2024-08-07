package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThreadService {
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Cache cache;

    public void get10SuggestedThreads() {
        System.out.println("10 suggested threads");
        System.out.println(cache.getSuggestedThreads());
    }

    /**
    public void get10LatestDicussionThreads() {
        System.out.println("10 latest Dicussion Threads");

        System.out.println(cache.getLatestDicussionThreads());
    }
     **/

    public List<Thread> getAllThreadsByCategory(String category) {
        System.out.println("All threads by category: " + category);
        return threadRepository.findByCategory(category);
    }
    public List<Thread> getAllThreadByOwnerId(Long ownerId) {
        System.out.println("All threads by owner id: " + ownerId);
        return threadRepository.findByOwnerId(ownerId);
    }

    public Thread createThread(ThreadRequestDTO threadRequestDTO) {
        Member author = userRepository.findById(threadRequestDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Thread thread = new Thread(threadRequestDTO.getTitle(), LocalDateTime.now());
        thread.setOwner(author);
        return threadRepository.save(thread);
    }

    public Thread updateThread(Long threadId, ThreadRequestDTO threadRequestDTO) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        thread.setName(threadRequestDTO.getTitle());
        thread.setCategory(threadRequestDTO.getCategory().toString());

        return threadRepository.save(thread);
    }


}
