package com.gymhub.gymhub.in_memory;

import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import com.gymhub.gymhub.helper.HelperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import static com.gymhub.gymhub.helper.HelperMethod.convertStringToxicStatusToBooleanValue;

@Component
public class CacheManipulation {
    @Autowired
    Cache cache;

    public boolean addUserToCache(long userId) {
        cache.getAllMemberID().put(userId, userId);
        cache.getPostLikeListByUser().put(userId, new HashSet<>());
        cache.getThreadLikeListByUser().put(userId, new HashSet<>());
        cache.getPostListByUser().put(userId, new HashSet<>());
        cache.getThreadListByUser().put(userId, new HashSet<>());
        return true;
    }

    public boolean addThreadToCache(long threadId, ThreadCategoryEnum category, ToxicStatusEnum toxicStatus, long authorId, boolean resolveStatus, String reason) {
        // Store the thread ID
        cache.getAllThreadID().put(threadId, threadId);

        // Initialize thread parameters
        ConcurrentHashMap<String, Object> threadParaMap = new ConcurrentHashMap<>();
        threadParaMap.put("ThreadID", threadId);
        threadParaMap.put("LikeCount", 0);
        threadParaMap.put("ViewCount", 0);
        threadParaMap.put("PostCount", 0);
        threadParaMap.put("CreationDate", System.currentTimeMillis());
        threadParaMap.put("ResolveStatus", resolveStatus ? 1 : 0);
        threadParaMap.put("Reason", reason);
        int toxicStatusBooleanNumber = HelperMethod.convertStringToxicStatusToBooleanValue(toxicStatus);

        threadParaMap.put("ToxicStatus", toxicStatusBooleanNumber);

        // Add thread parameters to the cache
        cache.getParametersForAllThreads().put(threadId, threadParaMap);

        // Manage thread list by category and status
        cache.getThreadListByCategoryAndToxicStatus().computeIfAbsent((category), k -> new HashMap<>());
        cache.getThreadListByCategoryAndToxicStatus().get(category).computeIfAbsent(toxicStatusBooleanNumber, k -> new LinkedList<>());
        cache.getThreadListByCategoryAndToxicStatus().get(category).get(toxicStatusBooleanNumber).add(threadId);

        // Initialize thread list by user if necessary
        cache.getThreadListByUser().computeIfAbsent(authorId, k -> new HashSet<>());
        cache.getThreadListByUser().get(authorId).add(threadId);

        // Initialize post list for this thread by toxic status
        cache.getPostListByThreadIdAndToxicStatus().put(threadId, new HashMap<>());
        cache.getPostListByThreadIdAndToxicStatus().get(threadId).put(1, new LinkedList<>());
        cache.getPostListByThreadIdAndToxicStatus().get(threadId).put(0, new LinkedList<>());
        cache.getPostListByThreadIdAndToxicStatus().get(threadId).put(-1, new LinkedList<>());

        return true;
    }



    public boolean addPostToCache(long postId, long threadId, long userId, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        // Store the post ID
        cache.getAllPostId().put(postId, postId);

        // Convert toxicStatus to a boolean number (1 for non-toxic, 0 for pending, -1 for toxic)
        int toxicStatusBooleanNumber = HelperMethod.convertStringToxicStatusToBooleanValue(toxicStatus);

        // Add the post to the appropriate list based on its toxic status
        cache.getPostListByThreadIdAndToxicStatus().computeIfAbsent(threadId, k -> new HashMap<>());
        cache.getPostListByThreadIdAndToxicStatus().get(threadId).computeIfAbsent(toxicStatusBooleanNumber, k -> new LinkedList<>());
        LinkedList<Long> toxicPostList = cache.getPostListByThreadIdAndToxicStatus().get(threadId).get(toxicStatusBooleanNumber);
        toxicPostList.add(postId);

        // Initialize the post parameters
        ConcurrentHashMap<String, Object> postParaMap = new ConcurrentHashMap<>();
        postParaMap.put("LikeCount", 0);
        postParaMap.put("CreationDate", System.currentTimeMillis());
        postParaMap.put("ToxicStatus", toxicStatusBooleanNumber);
        postParaMap.put("ResolveStatus", resolveStatus ? 1 : 0);
        postParaMap.put("Reason", reason);

        // Update the thread's latest post creation date if the post is non-toxic
        if (toxicStatusBooleanNumber == 1) {
            // Ensure threadParaMap is not null
            ConcurrentHashMap<String, Object> threadParaMap = cache.getParametersForAllThreads().computeIfAbsent(threadId, k -> new ConcurrentHashMap<>());
            // Initialize threadParaMap if it doesn't exist
            threadParaMap.put("PostCreationDate", System.currentTimeMillis());
        }

        // Store the post parameters in the cache
        cache.getParametersForAllPosts().put(postId, postParaMap);

        // Add the post to the user's list of posts, ensuring the list is initialized
        cache.getPostListByUser().computeIfAbsent(userId, k -> new HashSet<>()).add(postId);
        return true;
        }



    public boolean changeThreadToxicStatus(long threadId, ThreadCategoryEnum category, ToxicStatusEnum newStatus, String reason){
        int newToxicStatusBooleanValue = convertStringToxicStatusToBooleanValue(newStatus);

        // Retrieve and update the thread parameters
        ConcurrentHashMap<String, Object> threadParaMap = cache.getParametersForAllThreads().get(threadId);
        if (threadParaMap == null) return false;
        int oldToxicStatus = (Integer) threadParaMap.get("ToxicStatus");
        threadParaMap.put("ToxicStatus", newToxicStatusBooleanValue);
        threadParaMap.put("ResolveStatus", 1);
        threadParaMap.put("Reason", reason);
        // Update the thread list by category and status
        LinkedList<Long> oldStatusList = cache.getThreadListByCategoryAndToxicStatus().get(category).get(oldToxicStatus);
        if (oldStatusList != null) oldStatusList.remove(threadId);
        cache.getThreadListByCategoryAndToxicStatus().get(category).computeIfAbsent(newToxicStatusBooleanValue, k -> new LinkedList<>()).add(threadId);
        return true;
    }

    public boolean changePostToxicStatus(long postId, long threadId, ToxicStatusEnum newToxicStatus, String reason){
        int newToxicStatusBooleanValue = convertStringToxicStatusToBooleanValue(newToxicStatus);
        // Retrieve and update the post parameters
        ConcurrentHashMap<String, Object> postParaMap = cache.getParametersForAllPosts().get(postId);
        if (postParaMap == null) return false;
        int oldToxicStatus = (Integer) postParaMap.get("ToxicStatus");
        postParaMap.put("ToxicStatus", newToxicStatusBooleanValue);
        postParaMap.put("ResolveStatus", 1);
        postParaMap.put("Reason", reason);
        // Update the post list by thread ID and toxic status
        LinkedList<Long> oldStatusList = cache.getPostListByThreadIdAndToxicStatus().get(threadId).get(oldToxicStatus);
        if (oldStatusList != null) oldStatusList.remove(postId);
        cache.getPostListByThreadIdAndToxicStatus().get(threadId).computeIfAbsent(newToxicStatusBooleanValue, k -> new LinkedList<>()).add(postId);
        return true;
    }

    public boolean likePost(long postId, long userId, long threadId, int mode) {
        ConcurrentHashMap<String, Object> postParaMap = cache.getParametersForAllPosts().get(postId);
        ConcurrentHashMap<String, Object> threadParaMap = cache.getParametersForAllThreads().get(threadId);

        if (mode == 1) {
            postParaMap.put("LikeCount", (Integer) postParaMap.get("LikeCount") + 1);
            threadParaMap.put("LikeCount", (Integer) threadParaMap.get("LikeCount") + 1);
        } else if (mode == 0) {
            postParaMap.put("LikeCount", (Integer) postParaMap.get("LikeCount") - 1);
            threadParaMap.put("LikeCount", (Integer) threadParaMap.get("LikeCount") - 1);
        } else {
            return false;
        }
        cache.getPostLikeListByUser().get(userId).add(postId);
        return true;
    }



    }
