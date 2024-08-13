package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ReportThreadRequestDTO;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.dto.UpdateThreadTitleDTO;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.dto.ThreadResponseDTO;
import com.gymhub.gymhub.mapper.ThreadMapper;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
//helper method
//    public List<ThreadResponseDTO> findThreadFromDatabaseViaCacheThreadId(List<Long> ids) {
//        //get all thread ids from cache
//
//        //pass
//    }

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

    public List<ThreadResponseDTO> getAllThreadsByCategory(String category, int limit, int offset) {
        // Retrieve thread IDs from the cache based on the category
        List<Long> threadListByCategoryAndStatus = Optional.ofNullable(
                        inMemoryRepository.getThreadListByCategoryAndStatus(category))
                .map(statusMap -> statusMap.get(1)) // Assuming status 1 for non-toxic threads
                .orElse(new LinkedList<>());

        // Initialize a list to store the DTOs
        List<ThreadResponseDTO> threadResponseDTOs = new ArrayList<>();

        // Iterate through the thread IDs with the given offset and limit
        int count = 0;
        for (int i = offset; i < threadListByCategoryAndStatus.size() && count < limit; i++) {
            Long threadId = threadListByCategoryAndStatus.get(i);

            // Retrieve thread parameters from the cache
            ConcurrentHashMap<String, Number> threadParams = cache.getParametersForAllThreads().get(threadId);

            // Retrieve the rest of the details from the database
            Thread thread = threadRepository.findById(threadId).orElse(null);

            if (thread != null) {
                // Generate the DTO using the mapper
                ThreadResponseDTO dto = threadMapper.toThreadResponseDTO(thread, threadParams.mappingCount());
                if (dto != null) {
                    threadResponseDTOs.add(dto);
                    count++;
                }
            }
        }

        return threadResponseDTOs;
    }

    //Add limit and offset to the parameter list
    //Loop through the threadListByUser in the cache
    //For each of the thread id, find the corresponding parameters from the parametersForAllThreads
    //Then find the rest of the information from the  database

    public List<ThreadResponseDTO> getAllThreadByOwnerId(Long ownerId, int limit, int offset) {
        return threadRepository.findByOwnerId(ownerId).stream()
                .map(thread -> threadMapper.toThreadResponseDTO(thread, ownerId))
                .collect(Collectors.toList());

    }

    public boolean createThread(ThreadRequestDTO threadRequestDTO) {
        Member owner = userRepository.findById(threadRequestDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        long id = random.nextLong(50);
        System.out.println("Generated ID: " + id);

        String category = threadRequestDTO.getCategory().name();
        System.out.println(category);

        Thread thread = new Thread(id, threadRequestDTO.getTitle(), category,LocalDateTime.now());
        thread.setOwner(owner);
        String toxicStatus = "notToxic"; //Call the AI here
        inMemoryRepository.addThreadToCache(thread.getId(), threadRequestDTO.getCategory().name(), toxicStatus, owner.getId());
        threadRepository.save(thread);
        return true;
    }

    public boolean reportThread(ReportThreadRequestDTO reportThreadRequestDTO){
        return inMemoryRepository.changeThreadStatus(reportThreadRequestDTO.getId(), reportThreadRequestDTO.getThreadCategory().name(),
                reportThreadRequestDTO.getFrom(), reportThreadRequestDTO.getTo(), reportThreadRequestDTO.getReason());
    }

    public ResponseEntity<ThreadResponseDTO> updateThread(UpdateThreadTitleDTO updateThreadTitleDTO) {

        // Fetch the thread using the threadId from the DTO
        Thread thread = threadRepository.findById(updateThreadTitleDTO.getThreadId())
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        // Check if the author of the thread matches the user making the request
        if (!thread.getOwner().getId().equals(updateThreadTitleDTO.getUserId())) {
            // Return a forbidden response if the IDs do not match
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Update the thread title


        // Save the updated thread
        thread = threadRepository.save(thread);

        // Convert the updated thread to a DTO and return it in the response
        ThreadResponseDTO responseDTO = threadMapper.toThreadResponseDTO(thread, thread.getOwner().getId());
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    //report thread
}
