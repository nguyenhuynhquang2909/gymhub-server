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
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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
@Autowired
    private ThreadMapper threadMapper;
    @Autowired
    private Cache cache;

    //Return the following for this method HashMap<String, List<ThreadResponseDTO>>
    public HashMap<String, List<ThreadResponseDTO>> get10SuggestedThreads() {
        // Example usage of the cache to get suggested threads
        // The cache returns a HashMap with a list of thread IDs
        HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> suggestedThreads = inMemoryRepository.getSuggestedThreads();
        System.out.println("SuggestedThreads: " + suggestedThreads);
        HashMap<String, List<ThreadResponseDTO>> returnCollection = new HashMap<>();
        for (String key : suggestedThreads.keySet()) {

            List<ThreadResponseDTO> threadList = new LinkedList<>();
            List<Long> threadIds = new LinkedList<>();
            for (HashMap<String, Number> map: suggestedThreads.get(key).values()){
                System.out.println(map);
                Long threadId = (Long) map.get("ThreadID");
                threadIds.add(threadId);
                //Put all threadId into a list
                System.out.println("threadId: " + threadId);
                Thread thread = threadRepository.findById(threadId)
                        .orElseThrow(() -> new RuntimeException("Thread not found"));
                threadList.add(threadMapper.toThreadResponseDTO(thread, null));

            }

        }

        return returnCollection;
    }

    //Add limit and offset to the parameter
    //Loop through the threadListByCategoryAndStatus in cache starting from the offset
    //Retrieve the thread parameters from the cache's parametersForAllThreads
    //Retrieve the rest of the detail from the database
    //Generate the DTO

    public List<ThreadResponseDTO> getAllThreadsByCategory(String category) {
        return threadRepository.findByCategory(category).stream()
                .map(thread -> threadMapper.toThreadResponseDTO(thread, null)) // Pass memberId if needed
                .collect(Collectors.toList());
    }

    //Add limit and offset to the parameter list
    //Loop through the threadListByUser in the cache
    //For each of the thread id, find the corresponding parameters from the parametersForAllThreads
    //Then find the rest of the information from the  database

    public List<ThreadResponseDTO> getAllThreadByOwnerId(Long ownerId) {
        return threadRepository.findByOwnerId(ownerId).stream()
                .map(thread -> threadMapper.toThreadResponseDTO(thread, ownerId))
                .collect(Collectors.toList());

    }

    public boolean createThread(ThreadRequestDTO threadRequestDTO) {
        Member owner = userRepository.findById(threadRequestDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        long id = random.nextLong(50);
        System.out.println(id);
        Thread thread = new Thread(id, threadRequestDTO.getTitle(), LocalDateTime.now());
        thread.setOwner(owner);
        int status = 1; //Call the AI here
        inMemoryRepository.addThreadToCache(id, threadRequestDTO.getCategory().name(), status, owner.getId());
        threadRepository.save(thread);
        return true;
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
