package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ReportRequestDTO;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class ThreadService {
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InMemoryRepository inMemoryRepository;
    private Random random = new Random();

    public void get10SuggestedThreads() {
        System.out.println("10 suggested threads");
        System.out.println(inMemoryRepository.getSuggestedThreads());
    }
    
    public List<Thread> getAllThreadsByCategory(String category) {
        System.out.println("All threads by category: " + category);
        return threadRepository.findByCategory(category);
    }
    public List<Thread> getAllThreadByOwnerId(Long ownerId) {
        System.out.println("All threads by owner id: " + ownerId);
        return threadRepository.findByOwnerId(ownerId);
    }

    public Thread createThread(ThreadRequestDTO threadRequestDTO) {
        Member owner = userRepository.findById(threadRequestDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        long id = random.nextLong(50);
        Thread thread = new Thread(id, threadRequestDTO.getTitle(), LocalDateTime.now());
        thread.setOwner(owner);
        int status = 0; //Call the AI here
        inMemoryRepository.addThreadToCache(id, threadRequestDTO.getCategory().name(), status, owner.getId());
        return threadRepository.save(thread);
    }

    public boolean reportThread(ReportRequestDTO reportRequestDTO){
        return inMemoryRepository.changeThreadStatus(reportRequestDTO.getId(), reportRequestDTO.getThreadCategory().name(),
                reportRequestDTO.getFrom(), reportRequestDTO.getTo(), reportRequestDTO.getReason());
    }


    public Thread updateThread(Long threadId, ThreadRequestDTO threadRequestDTO) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        thread.setName(threadRequestDTO.getTitle());
        thread.setCategory(threadRequestDTO.getCategory().toString());

        return threadRepository.save(thread);
    }


}
