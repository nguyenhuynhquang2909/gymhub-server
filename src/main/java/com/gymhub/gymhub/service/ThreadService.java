package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.helper.HelperMethod;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.mapper.ThreadMapper;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.MemberRepository;
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
    private MemberRepository memberRepository;
    @Autowired
    private InMemoryRepository inMemoryRepository;

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
    //Add limit and offset to the parameter
    //Loop through the threadListByCategoryAndStatus in cache starting from the offset
    //Retrieve the thread parameters from the cache's parametersForAllThreads
    //Retrieve the rest of the detail from the database
    //Generate the DTO

    public List<ThreadResponseDTO> getAllThreadsByCategory(ThreadCategoryEnum category, int limit, int offset) {
        // Retrieve thread IDs from the cache based on the category
        List<Long> threadListByCategoryAndStatus = Optional.ofNullable(
                        inMemoryRepository.getAllThreadIdsByCategory(category))
                .map(statusMap -> statusMap.get(1)) // Assuming status 1 for non-toxic threads
                .orElse(new LinkedList<>());

        // Initialize a list to store the DTOs
        List<ThreadResponseDTO> threadResponseDTOs = new ArrayList<>();

        // Iterate through the thread IDs with the given offset and limit
        int count = 0;
        for (int i = offset; i < threadListByCategoryAndStatus.size() && count < limit; i++) {
            Long threadId = threadListByCategoryAndStatus.get(i);

            // Retrieve thread parameters from the cache
            ConcurrentHashMap<String, Object> threadParams = cache.getParametersForAllThreads().get(threadId);

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

    public List<ThreadResponseDTO> getAllThreadByOwnerId(Long authorId, int limit, int offset) {
        // Get the set of thread IDs for the given owner from the cache
        Set<Long> threadsCreatedByUser = cache.getThreadListByUser().get(authorId);

        // Convert the set of thread IDs to a list and apply offset and limit for pagination
        List<Long> paginatedThreadIds = threadsCreatedByUser.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        List<ThreadResponseDTO> threadResponseDTOList = new ArrayList<>();

        for (Long threadId : paginatedThreadIds) {
            // Get the thread parameters from the cache
            ConcurrentHashMap<String, Object> threadParams = cache.getParametersForAllThreads().get(threadId);

            if (threadParams != null) {
                // Retrieve additional information from the database
                Thread thread = threadRepository.findById(threadId).orElse(null);

                if (thread != null) {
                    // Map the thread and parameters to ThreadResponseDTO
                    ThreadResponseDTO threadResponseDTO = threadMapper.toThreadResponseDTO(thread, authorId);

                    // Populate additional fields from cache (optional based on your needs)
                    threadResponseDTO.setLikeCount((Integer) threadParams.get("LikeCount"));
                    threadResponseDTO.setViewCount((Integer) threadParams.get("ViewCount"));
                    threadResponseDTO.setPostCount((Integer) threadParams.get("PostCount"));
                    threadResponseDTO.setCreationDateTime((Long) threadParams.get("CreationDate"));
                    ToxicStatusEnum toxicStatus = HelperMethod.convertBooleanToxicStatusToStringValue((Integer) threadParams.get("toxicStatus"));
                    threadResponseDTO.setToxicStatus(toxicStatus);
                    threadResponseDTO.setResolveStatus((Boolean) threadParams.get("resolveStatus"));
                    threadResponseDTO.setReason((String) threadParams.get("reason"));

                    // Add the DTO to the list
                    threadResponseDTOList.add(threadResponseDTO);
                }
            }
        }

        return threadResponseDTOList;
    }


    public boolean createThread(ThreadRequestDTO threadRequestDTO) {
        Member owner = memberRepository.findById(threadRequestDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
       long id = HelperMethod.generateUniqueIds();
        System.out.println("Generated ID: " + id); //need tools for unique thread id
        String category = threadRequestDTO.getCategory().name();
        Thread thread = new Thread( id, threadRequestDTO.getTitle(), category,LocalDateTime.now());
        thread.setOwner(owner);
        //ADD Thread to cache
       ToxicStatusEnum tempToxicEnum = ToxicStatusEnum.NOT_TOXIC;  //temporary set ToxicStatus = NOT-TOXIC
        //Then Call the AI here to generate the toxicStatus
        inMemoryRepository.addThreadToCache(thread.getId(), threadRequestDTO.getCategory().name(), tempToxicEnum, owner.getId(), false, "");
        threadRepository.save(thread);
        return true;
    }

    public boolean reportThread(ThreadRequestDTO threadRequestDTO, String reason){
        return inMemoryRepository.changeThreadToxicStatusForMemberReporting(threadRequestDTO.getId(), threadRequestDTO.getCategory(),
                reason);
    }

    public ResponseEntity<ThreadResponseDTO> updateThreadTitle(UpdateThreadTitleDTO updateThreadTitleDTO) {

        // Fetch the thread using the threadId from the DTO
        Thread thread = threadRepository.findById(updateThreadTitleDTO.getThreadId())
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        // Check if the author of the thread matches the user making the request
        if (!thread.getOwner().getId().equals(updateThreadTitleDTO.getUserId())) {
            // Return a forbidden response if the IDs do not match
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        //update the thread resolveStatus
        //call the AI API ?
        // Update the thread title
        thread.setTitle(updateThreadTitleDTO.getTitle());

        // Save the updated thread
        thread = threadRepository.save(thread);


        // Convert the updated thread to a DTO and return it in the response
        ThreadResponseDTO responseDTO = threadMapper.toThreadResponseDTO(thread, thread.getOwner().getId());
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }



}
