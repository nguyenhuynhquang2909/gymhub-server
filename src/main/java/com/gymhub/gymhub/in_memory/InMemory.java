package com.gymhub.gymhub.in_memory;

import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.in_memory.custom_data_structure.ZSet;
import org.aspectj.weaver.Lint;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.SubmissionPublisher;

@Component
public class InMemory {
    ZSet<Integer> nonToxicFlexingThreadSortedByLikes = new ZSet<>();
    ZSet<Integer> pendingFlexingThreadSetSortedByLikes = new ZSet<>();
    ZSet<Integer> nonToxicFlexingThreadSortedByViews = new ZSet<>();
    ZSet<Integer> pendingFlexingThreadSetSortedByViews = new ZSet<>();
    ZSet<Integer> nonToxicFlexingThreadSortedByRelevance = new ZSet<>();
    ZSet<Integer> pendingFlexingThreadSetSortedByRelevance = new ZSet<>();

    ZSet<Integer> nonToxicAdvisesThreadSortedByLikes = new ZSet<>();
    ZSet<Integer> pendingAdvisesThreadSetSortedByLikes = new ZSet<>();
    ZSet<Integer> nonToxicAdvisesThreadSortedByViews = new ZSet<>();
    ZSet<Integer> pendingAdvisesThreadSetSortedByViews = new ZSet<>();
    ZSet<Integer> nonToxicAdvisesThreadSortedByRelevance = new ZSet<>();
    ZSet<Integer> pendingAdvisesThreadSetSortedByRelevance = new ZSet<>();

    ZSet<Integer> nonToxicSupplementThreadSortedByLikes = new ZSet<>();
    ZSet<Integer> pendingSupplementThreadSetSortedByLikes = new ZSet<>();
    ZSet<Integer> nonToxicSupplementThreadSortedByViews = new ZSet<>();
    ZSet<Integer> pendingSupplementThreadSetSortedByViews = new ZSet<>();
    ZSet<Integer> nonToxicSupplementThreadSortedByRelevance = new ZSet<>();
    ZSet<Integer> pendingSupplementThreadSetSortedByRelevance = new ZSet<>();

    LinkedHashMap<Long, LocalDateTime> nonToxicFlexingThreadMap = new LinkedHashMap<>();
    LinkedHashMap<Long, LocalDateTime> pendingFlexingThreadMap = new LinkedHashMap<>();
    LinkedHashMap<Long, LocalDateTime> nonToxicAdvisesThreadMap = new LinkedHashMap<>();
    LinkedHashMap<Long, LocalDateTime> pendingAdvisesThreadMap = new LinkedHashMap<>();
    LinkedHashMap<Long, LocalDateTime> nonToxicSupplementThreadMap = new LinkedHashMap<>();
    LinkedHashMap<Long, LocalDateTime> pendingSupplementThreadMap = new LinkedHashMap<>();

    HashMap<Long, HashMap<String, Integer>> postInfo = new HashMap<>();
    HashMap<Long, LinkedHashSet<Long>> nonToxicPostsByThreadId = new HashMap<>();
    HashMap<Long, LinkedHashSet<Long>> pendingPostsByThreadId = new HashMap<>();
    HashMap<Long, LinkedHashSet<Long>> nonToxicPostsByUserId = new HashMap<>();
    HashMap<Long, LinkedHashSet<Long>> pendingPostsByUserId = new HashMap<>();

    ZSet<Long> bannedUserSet = new ZSet<>();

    HashMap<Long, Set<Long>> userLikeSet = new HashMap<>();
    HashMap<Long, Set<Long>> userFollowersSet = new HashMap<>();
    Set<Long> reportedPost = new HashSet<>();
    Set<Long> reportedThread = new HashSet<>();

    public boolean getTopLikedThreads(int limit, SubmissionPublisher<HashMap<String, Number>> publisher){
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicFlexingThreadSortedByLikes.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicFlexingThreadSortedByLikes.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", key);
                        thread.put("View Count", nonToxicFlexingThreadSortedByViews.getMap().get(id));
                        publisher.submit(thread);
                    }
                    count = count + nonToxicFlexingThreadSortedByLikes.getTreeMap().get(key).size();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicSupplementThreadSortedByLikes.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicSupplementThreadSortedByLikes.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", key);
                        thread.put("View Count", nonToxicSupplementThreadSortedByViews.getMap().get(id));
                        publisher.submit(thread);
                    }
                    count = count + nonToxicSupplementThreadSortedByLikes.getTreeMap().get(key).size();
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicAdvisesThreadSortedByLikes.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicAdvisesThreadSortedByLikes.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", key);
                        thread.put("View Count", nonToxicAdvisesThreadSortedByViews.getMap().get(id));
                        publisher.submit(thread);
                    }
                    count = count + nonToxicAdvisesThreadSortedByLikes.getTreeMap().get(key).size();
                }
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        return true;

    }

    public boolean getTopViewedThreads(int limit, SubmissionPublisher<HashMap<String, Number>> publisher){
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicFlexingThreadSortedByViews.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicFlexingThreadSortedByViews.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", nonToxicFlexingThreadSortedByLikes.getMap().get(id));
                        thread.put("View Count", key);
                        publisher.submit(thread);
                    }
                    count = count + nonToxicFlexingThreadSortedByViews.getTreeMap().get(key).size();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicSupplementThreadSortedByViews.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicSupplementThreadSortedByViews.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", nonToxicSupplementThreadSortedByLikes.getMap().get(id));
                        thread.put("View Count", key);
                        publisher.submit(thread);
                    }
                    count = count + nonToxicSupplementThreadSortedByViews.getTreeMap().get(key).size();
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicAdvisesThreadSortedByViews.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicAdvisesThreadSortedByViews.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", nonToxicAdvisesThreadSortedByLikes.getMap().get(id));
                        thread.put("View Count", key);
                        publisher.submit(thread);
                    }
                    count = count + nonToxicAdvisesThreadSortedByViews.getTreeMap().get(key).size();
                }
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        return true;

    }

    public boolean getTopRecentThreads(int limit, SubmissionPublisher<HashMap<String, Number>> publisher){
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicFlexingThreadSortedByRelevance.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicFlexingThreadSortedByRelevance.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", nonToxicFlexingThreadSortedByLikes.getMap().get(id));
                        thread.put("View Count", nonToxicFlexingThreadSortedByViews.getMap().get(id));
                        publisher.submit(thread);
                    }
                    count = count + nonToxicFlexingThreadSortedByLikes.getTreeMap().get(key).size();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicSupplementThreadSortedByRelevance.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicSupplementThreadSortedByRelevance.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", nonToxicFlexingThreadSortedByLikes.getMap().get(id));
                        thread.put("View Count", nonToxicFlexingThreadSortedByViews.getMap().get(id));
                        publisher.submit(thread);
                    }
                    count = count + nonToxicSupplementThreadSortedByRelevance.getTreeMap().get(key).size();
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<Integer> iterator = nonToxicAdvisesThreadSortedByRelevance.getTreeMap().keySet().iterator();
                int count = 0;
                while(iterator.hasNext() && count <limit){
                    int key = iterator.next();
                    for (Long id: nonToxicAdvisesThreadSortedByRelevance.getTreeMap().get(key)){
                        HashMap<String, Number> thread = new HashMap<>();
                        thread.put("id", id);
                        thread.put("Like Count", nonToxicFlexingThreadSortedByLikes.getMap().get(id));
                        thread.put("View Count", nonToxicFlexingThreadSortedByViews.getMap().get(id));
                        publisher.submit(thread);
                    }
                    count = count + nonToxicAdvisesThreadSortedByRelevance.getTreeMap().get(key).size();
                }
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        return true;

    }

    public boolean getFlexingThread(int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher){
        int count = 0;
        List<Map.Entry<Long, LocalDateTime>> keyValuePairs = new ArrayList<>(nonToxicFlexingThreadMap.entrySet());
        for (int i = offset; i < keyValuePairs.size() && count < limit; i++){
            long key = keyValuePairs.get(i).getKey();
            HashMap<String, Number> thread = new HashMap<>();
            thread.put("id", key);
            thread.put("Like Count", nonToxicFlexingThreadSortedByLikes.getMap().get(key));
            thread.put("View Count", nonToxicFlexingThreadSortedByViews.getMap().get(key));
            publisher.submit(thread);
            count++;

        }
        return true;
    }

    public boolean getAdvisesThread(int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher){
        int count = 0;
        List<Map.Entry<Long, LocalDateTime>> keyValuePairs = new ArrayList<>(nonToxicAdvisesThreadMap.entrySet());
        for (int i = offset; i < keyValuePairs.size() && count < limit; i++){
            long key = keyValuePairs.get(i).getKey();
            HashMap<String, Number> thread = new HashMap<>();
            thread.put("id", key);
            thread.put("Like Count", nonToxicAdvisesThreadSortedByLikes.getMap().get(key));
            thread.put("View Count", nonToxicAdvisesThreadSortedByViews.getMap().get(key));
            publisher.submit(thread);
            count++;

        }
        return true;
    }

    public boolean getSupplementThread(int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher){
        int count = 0;
        List<Map.Entry<Long, LocalDateTime>> keyValuePairs = new ArrayList<>(nonToxicSupplementThreadMap.entrySet());
        for (int i = offset; i < keyValuePairs.size() && count < limit; i++){
            long key = keyValuePairs.get(i).getKey();
            HashMap<String, Number> thread = new HashMap<>();
            thread.put("id", key);
            thread.put("Like Count", nonToxicSupplementThreadSortedByLikes.getMap().get(key));
            thread.put("View Count", nonToxicSupplementThreadSortedByViews.getMap().get(key));
            publisher.submit(thread);
            count++;

        }
        return true;
    }





}
