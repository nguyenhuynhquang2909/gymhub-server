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

    public HashMap<String, List<ThreadResponseDTO>> get10SuggestedThreads() {
        HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> suggestedThreads = inMemoryRepository.getSuggestedThreads();
        HashMap<String, List<ThreadResponseDTO>> returnCollection = new HashMap<>();

        for (String key : suggestedThreads.keySet()) {
            List<ThreadResponseDTO> threadList = new LinkedList<>();
            for (HashMap<String, Number> map : suggestedThreads.get(key).values()) {
                Long threadId = (Long) map.get("ThreadID");
                Thread thread = threadRepository.findById(threadId)
                        .orElseThrow(() -> new RuntimeException("Thread not found"));
                threadList.add(threadMapper.toThreadResponseDTO(thread));
            }
            returnCollection.put(key, threadList);
        }

        return returnCollection;
    }

    public List<ThreadResponseDTO> getAllThreadsByCategory(ThreadCategoryEnum category, int limit, int offset) {
        List<Long> threadListByCategoryAndStatus = Optional.ofNullable(
                        inMemoryRepository.getAllThreadIdsByCategory(category))
                .map(statusMap -> statusMap.get(1)) // Assuming status 1 for non-toxic threads
                .orElse(new LinkedList<>());
        System.out.println("Supplement thread id : " + threadListByCategoryAndStatus);

        List<ThreadResponseDTO> returnList =mapThreadListToThreadResponseDTOList(threadListByCategoryAndStatus);
        System.out.println("List of Thread DTOs " + returnList);
         return  returnList ;


    }

    public List<ThreadResponseDTO> getAllThreadByOwnerId(Long authorId, int limit, int offset) {
        Set<Long> threadsCreatedByUser = cache.getThreadListByUser().get(authorId);
        List<Long> paginatedThreadIds = threadsCreatedByUser.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        List<ThreadResponseDTO> threadResponseDTOList = new ArrayList<>();

        for (Long threadId : paginatedThreadIds) {
            ConcurrentHashMap<String, Object> threadParams = cache.getParametersForAllThreads().get(threadId);
            Thread thread = threadRepository.findById(threadId).orElse(null);

            if (thread != null) {
                ThreadResponseDTO threadResponseDTO = threadMapper.toThreadResponseDTO(thread);
                threadResponseDTO.setLikeCount((Integer) threadParams.get("LikeCount"));
                threadResponseDTO.setViewCount((Integer) threadParams.get("ViewCount"));
                threadResponseDTO.setPostCount((Integer) threadParams.get("PostCount"));
                threadResponseDTO.setCreationDateTime((Long) threadParams.get("CreationDate"));
                ToxicStatusEnum toxicStatus = HelperMethod.convertBooleanToxicStatusToStringValue((Integer) threadParams.get("toxicStatus"));
                threadResponseDTO.setToxicStatus(toxicStatus);
                threadResponseDTO.setResolveStatus((Boolean) threadParams.get("resolveStatus"));
                threadResponseDTO.setReason((String) threadParams.get("reason"));

                threadResponseDTOList.add(threadResponseDTO);
            }
        }

        return threadResponseDTOList;
    }

    public void createThread(Long memberId, ThreadRequestDTO threadRequestDTO) {
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        long id = HelperMethod.generateUniqueIds();
        Thread thread = new Thread(id, threadRequestDTO.getTitle(), threadRequestDTO.getCategory().name(), LocalDateTime.now());
        thread.setOwner(owner);

        ToxicStatusEnum tempToxicEnum = ToxicStatusEnum.NOT_TOXIC;
        inMemoryRepository.addThreadToCache(thread.getId(), threadRequestDTO.getCategory().name(), tempToxicEnum, owner.getId(), false, "");
        threadRepository.save(thread);
    }

    public boolean reportThread(ThreadRequestDTO threadRequestDTO, String reason) {
        return inMemoryRepository.changeThreadToxicStatusForMemberReporting(threadRequestDTO.getId(), threadRequestDTO.getCategory(),
                reason);
    }

    public boolean updateThreadTitle(Long memberId, UpdateThreadTitleDTO updateThreadTitleDTO) {
        try {
            Thread thread = threadRepository.findById(updateThreadTitleDTO.getThreadId())
                    .orElseThrow(() -> new RuntimeException("Thread not found"));

            if (!thread.getOwner().getId().equals(memberId)) {
                return false; // User is not authorized to update this thread
            }

            thread.setTitle(updateThreadTitleDTO.getTitle());
            threadRepository.save(thread);
            return true; // Operation succeeded
        } catch (Exception e) {
            return false; // Operation failed due to exception
        }
    }

    public List<Thread> getAllThreadsByListOfIds(List<Long> threadIds) {

        List<Thread> returnList = threadRepository.findAllByIdsWithOwner(threadIds);

        return  returnList;

    }

    public List<ThreadResponseDTO> mapThreadListToThreadResponseDTOList(List<Long> threadIds) {
        List<Thread> threadList = getAllThreadsByListOfIds(threadIds);
        List<ThreadResponseDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < threadList.size(); i++) {
            try {
                Thread thread = threadList.get(i);
                ThreadResponseDTO threadResponseDTO = threadMapper.toThreadResponseDTO(thread);
                dtoList.add(threadResponseDTO);
            } catch (Exception e) {
                System.err.println("Error mapping thread: " + threadList.get(i).getId() + ", " + e.getMessage());
                e.printStackTrace();
            }
        }
        return dtoList;
    }




}
