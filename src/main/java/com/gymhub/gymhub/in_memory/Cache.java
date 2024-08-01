package com.gymhub.gymhub.in_memory;

import io.swagger.v3.oas.models.links.Link;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Flow;

/**
 * The type Cache.
 */

@Component
public class Cache {

    LinkedHashMap<Long, ConcurrentHashMap<String, Number>> parametersForAllThreads = new LinkedHashMap<>();
    HashMap<Long, ConcurrentHashMap<String, Number>> parametersForAllPosts = new LinkedHashMap<>();
    HashMap<String, HashMap<Integer, LinkedList<Long>>> threadListByCategoryAndStatus = new HashMap<>();
    HashMap<Long, HashMap<String, LinkedList<Long>>> postListByThreadIdAndStatus =  new HashMap<>();
    HashMap<Long, Set<Long>> postLikeListByUser = new HashMap<>();
    HashMap<Long, Set<Long>> threadLikeListByUser = new HashMap<>();
    HashMap<Long, Set<Long>> postListByUser = new HashMap<>();
    HashMap<Long, Set<Long>> threadListByUser = new HashMap<>();
    Set<Long> reportedThreads = new HashSet<>();
    Set<Long> reportedPosts = new HashSet<>();

    public TreeMap<Double, HashMap<String, Number>> getMostTrendingThreads(long userId){
        Iterator<Long> iterator = parametersForAllThreads.keySet().iterator();
        TreeMap<Double, HashMap<String, Number>> returnCollection = new TreeMap<>();
        while(iterator.hasNext()){
            Long currentKey = iterator.next();
            ConcurrentHashMap<String, Number> threadParaMap = parametersForAllThreads.get(currentKey);
            if (threadParaMap.get("Status").equals(1)){
                double score = getThreadRelevancy(threadParaMap);
                HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, userId, currentKey);
                returnCollection.put(score, returnedMap);
            }
        }
        return returnCollection;
    }

    public boolean addThreadToCache(long threadId, String category, int status, long userId){
        ConcurrentHashMap<String, Number> threadParaMap = new ConcurrentHashMap<>();
        threadParaMap.put("LikeCount", 0);
        threadParaMap.put("ViewCount", 0);
        threadParaMap.put("PostCount", 1);
        threadParaMap.put("ThreadCreationDate", System.currentTimeMillis());
        threadParaMap.put("Status", status);
        parametersForAllThreads.put(threadId, threadParaMap);
        threadListByCategoryAndStatus.get(category).get(status).add(threadId);
        threadListByUser.get(userId).add(threadId);
        return true;
    }

    public boolean returnThreadByCategory(String category, Long userId, int limit, int offset, Flow.Publisher<Long> publisher){
        LinkedList<Long> nonToxicThreadsList  = threadListByCategoryAndStatus.get(category).get(1);
        Iterator<Long> iterator = nonToxicThreadsList.listIterator(offset);
        int count = 1;
        while(iterator.hasNext() && count <= limit){
            Long threadId = iterator.next();
            ConcurrentHashMap<String, Number> threadParaMap = parametersForAllThreads.get(threadId);
            HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, userId, threadId);
            publisher.p

        }
    }

    private HashMap<String, Number> returnThreadMapBuilder(ConcurrentHashMap<String, Number> cachedMap, Long userId, long threadId ){
        HashMap<String, Number> returnedMap = new HashMap<>();
        returnedMap.put("id", threadId);
        returnedMap.put("LikeCount", cachedMap.get("LikeCount"));
        returnedMap.put("PostCount", cachedMap.get("PostCount"));
        returnedMap.put("ViewCount", cachedMap.get("ViewCount"));
        if (userId == null){
            returnedMap.put("Like Status", 0);
        }
        else {
            if (threadLikeListByUser.get(userId).contains(threadId)){
                returnedMap.put("Like Status", 1);
            }
            else {
                returnedMap.put("Like Status", 0);
            }
        }


        if (reportedThreads.contains(threadId)){
            returnedMap.put("Report Status", 1);
        }
        else {
            returnedMap.put("Like Status", 0);
        }
        return returnedMap;

    }

    private double getThreadRelevancy(ConcurrentHashMap<String, Number> threadParaMap){
        long primitiveThreadCreationDate = (Long) threadParaMap.get("ThreadCreationDate").longValue();
        double distanceFromToday = (double) primitiveThreadCreationDate / System.currentTimeMillis();
        int likeNum = threadParaMap.get("LikeCount").intValue();
        int postNum = threadParaMap.get("PostCount").intValue();
        int viewNum = threadParaMap.get("ViewCount").intValue();
        return (distanceFromToday) * (likeNum + viewNum + postNum);

    }



}
