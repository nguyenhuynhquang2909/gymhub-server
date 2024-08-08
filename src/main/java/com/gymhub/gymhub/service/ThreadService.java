package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ReportRequestDTO;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.dto.ThreadResponseDTO;
import com.gymhub.gymhub.mapper.ThreadMapper;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.UserRepository;
import com.gymhub.gymhub.in_memory.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Service
public class ThreadService {
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InMemoryRepository inMemoryRepository;
    private Random random = new Random();

    private ThreadMapper threadMapper;
    @Autowired
    private Cache cache;

    public List<ThreadResponseDTO> get10SuggestedThreads() {
        // Example usage of the cache to get suggested threads
        // The cache returns a HashMap with a list of thread IDs
        HashMap<String, TreeMap<Double, HashMap<String, Number>>> suggestedThreads = inMemoryRepository.getSuggestedThreads();

        // Convert thread IDs to ThreadResponseDTO
        List<ThreadResponseDTO> responseList = new ArrayList<>();
        for (TreeMap<Double, HashMap<String, Number>> treeMap : suggestedThreads.values()) {
            for (HashMap<String, Number> threadData : treeMap.values()) {
                Long threadId = threadData.get("id").longValue();
                Thread thread = threadRepository.findById(threadId)
                        .orElseThrow(() -> new RuntimeException("Thread not found"));
                responseList.add(threadMapper.toThreadResponseDTO(thread, null)); // Pass memberId if needed
            }
        }
        return responseList;
    }


    public List<ThreadResponseDTO> getAllThreadsByCategory(String category) {
        return threadRepository.findByCategory(category).stream()
                .map(thread -> threadMapper.toThreadResponseDTO(thread, null)) // Pass memberId if needed
                .collect(Collectors.toList());
    }

    public List<ThreadResponseDTO> getAllThreadByOwnerId(Long ownerId) {
        return threadRepository.findByOwnerId(ownerId).stream()
                .map(thread -> threadMapper.toThreadResponseDTO(thread, ownerId))
                .collect(Collectors.toList());

    }

    public ThreadResponseDTO createThread(ThreadRequestDTO threadRequestDTO) {
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

    public ThreadResponseDTO updateThread(Long threadId, ThreadRequestDTO threadRequestDTO) {

        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        thread.setName(threadRequestDTO.getTitle());
        thread.setCategory(threadRequestDTO.getCategory().toString());

        thread = threadRepository.save(thread);
        return threadMapper.toThreadResponseDTO(thread, thread.getOwner().getId());
    }
}
