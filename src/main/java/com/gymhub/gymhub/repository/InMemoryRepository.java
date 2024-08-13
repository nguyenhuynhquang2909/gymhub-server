package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.actions.AddPostAction;
import com.gymhub.gymhub.actions.AddThreadAction;
import com.gymhub.gymhub.actions.AddUserAction;
import com.gymhub.gymhub.actions.ChangePostStatusAction;
import com.gymhub.gymhub.actions.ChangeThreadStatusAction;
import com.gymhub.gymhub.actions.LikePostAction;
import com.gymhub.gymhub.actions.MustLogAction;
import com.gymhub.gymhub.in_memory.Cache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.SubmissionPublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * TODO Create methods corresponding to the methods in the cache class with similar signatures
 * In each of their bodies, call the corresponding method from the cache class, and instantiate
 * Instantiate FileOutputStream and wrap it inside the ObjectOutputStream object
 * Instantiate an action object corresponding to the method
 * Write the action object to file
 * By default, the write method of the ObjectOutputStream will rewrite the entire file for every append
 * There is a way to change this behavior to appending to the end of the file In StackOverflow
 */

@Repository
public class InMemoryRepository {
    @Autowired
    Cache cache;

    private static final String LOG_FILE_PATH = "src/main/resources/logs/cache-actions.log";
    private static long actionIdCounter = 0;

    public void logAction(MustLogAction action) {
        try (FileOutputStream fos = new FileOutputStream(LOG_FILE_PATH, true);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean addUser(long userId) {
        boolean result = cache.addUser(userId);
        AddUserAction action = new AddUserAction(++actionIdCounter, userId);
        logAction(action);
        return result;
    }

    public boolean addThreadToCache(long threadId, String category, String toxicStatus, long userId) {
        boolean result = cache.addThreadToCache(threadId, category, toxicStatus, userId);
        AddThreadAction action = new AddThreadAction(++actionIdCounter, threadId, category, toxicStatus, userId);
        logAction(action);
        return result;
    }


    public boolean changeThreadStatusForReportingAndComplaining(long threadId, String category, int from, int to, String reason) {
        boolean result = cache.changeThreadStatusForReportingAndComplaining(threadId, category, from, to, reason);
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(++actionIdCounter, threadId, category, from, to, reason);
        logAction(action);
        return result;
    }

    public boolean changeThreadStatusFromModDashBoard(long threadId, String newStatus, String reason) {
        int toxicStatusBooleanValue = 0;
        if(newStatus.equals("NOT-TOXIC")){

        }
        if(newStatus.equals("TOXIC")){
            boolean result = cache.changeThreadStatusForModDashBoard(threadId,newStatus , reason);

        }


        ChangeThreadStatusAction action = new ChangeThreadStatusAction(++actionIdCounter, threadId, newStatus, reason);
        logAction(action);
        return result;
    }


    public boolean changePostStatus(long postId, long threadId, int from, int to, String reason) {
        boolean result = cache.changePostStatus(postId, threadId, from, to, reason);
        ChangePostStatusAction action = new ChangePostStatusAction(++actionIdCounter, postId, threadId, from, to, reason);
        logAction(action);
        return result;
    }

    public boolean addPostToCache(long postId, long threadId, long userId, String toxicStatus) {
        boolean result = cache.addPostToCache(threadId, postId, userId, toxicStatus);
        AddPostAction action = new AddPostAction(++actionIdCounter, threadId, postId, userId,toxicStatus);
        logAction(action);
        return result;
    }

    public boolean likePost(long postId, long userId, long threadId, int mode) {
        boolean result = cache.likePost(postId, userId, threadId, mode);
        LikePostAction action = new LikePostAction(++actionIdCounter, postId, userId, threadId, mode);
        logAction(action);
        return result;
    }

    public HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> getSuggestedThreads() {
        return cache.getSuggestedThreads();
    }

    public boolean returnThreadByCategory(String category, Long userId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher) {
        return cache.returnThreadByCategory(category, userId, limit, offset, publisher);
    }

    public boolean returnPostByThreadId(long threadId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher, Long userId) {
        return cache.returnPostByThreadId(threadId, limit, offset, publisher, userId);
    }

    public boolean checkBan(Long userId) {
        return cache.checkBan(userId);
    }


    public void restoreFromLog() {
        try (FileInputStream fis = new FileInputStream(LOG_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    MustLogAction action = (MustLogAction) ois.readObject();
                    if (action instanceof AddUserAction) {
                        cache.addUser(((AddUserAction) action).getUserId());
                    } else if (action instanceof AddThreadAction) {
                        AddThreadAction addThreadAction = (AddThreadAction) action;
                        cache.addThreadToCache(addThreadAction.getThreadId(), addThreadAction.getCategory(), addThreadAction.getStatus(), addThreadAction.getUserId());
                    } else if (action instanceof ChangeThreadStatusAction) {
                        ChangeThreadStatusAction changeThreadStatusAction = (ChangeThreadStatusAction) action;
                        cache.changeThreadStatusForReportingAndComplaining(changeThreadStatusAction.getThreadId(), changeThreadStatusAction.getCategory(), changeThreadStatusAction.getFrom(), changeThreadStatusAction.getTo(), changeThreadStatusAction.getReason());
                    } else if (action instanceof ChangePostStatusAction) {
                        ChangePostStatusAction changePostStatusAction = (ChangePostStatusAction) action;
                        cache.changePostStatus(changePostStatusAction.getPostId(), changePostStatusAction.getThreadId(), changePostStatusAction.getFrom(), changePostStatusAction.getTo(), changePostStatusAction.getReason());
                    } else if (action instanceof AddPostAction) {
                        AddPostAction addPostAction = (AddPostAction) action;
                        cache.addPostToCache(addPostAction.getThreadId(), addPostAction.getPostId(), addPostAction.getUserId(), addPostAction.getStatus());
                    } else if (action instanceof LikePostAction) {
                        LikePostAction likePostAction = (LikePostAction) action;
                        cache.likePost(likePostAction.getPostId(), likePostAction.getUserId(), likePostAction.getThreadId(), likePostAction.getMode());
                    }

                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, LinkedList<Long>> getThreadListByCategoryAndStatus(String category) {
        return cache.getThreadListByCategoryAndStatus().getOrDefault(category, new HashMap<>());
    }

    public HashMap<String, HashMap<Integer, LinkedList<Long>>> getPendingThreads() {
        HashMap<String, HashMap<Integer, LinkedList<Long>>> pendingThreadsByCategory = new HashMap<>();

        // Iterate through the threadListByCategoryAndStatus map to find all pending threads (status = 0)
        for (Map.Entry<String, HashMap<Integer, LinkedList<Long>>> categoryEntry : cache.getThreadListByCategoryAndStatus().entrySet()) {
            String category = categoryEntry.getKey();
            HashMap<Integer, LinkedList<Long>> threadsByStatus = categoryEntry.getValue();

            LinkedList<Long> pendingThreadsInCategory = threadsByStatus.get(0); // 0 represents PENDING status

            if (pendingThreadsInCategory != null) {
                HashMap<Integer, LinkedList<Long>> pendingStatusMap = new HashMap<>();
                pendingStatusMap.put(0, new LinkedList<>(pendingThreadsInCategory)); // Add only pending threads

                pendingThreadsByCategory.put(category, pendingStatusMap);
            }
        }

        return pendingThreadsByCategory;
    }



    public HashMap<Long, HashMap<Integer, LinkedList<Long>>> getPendingPosts() {
        HashMap<Long, HashMap<Integer, LinkedList<Long>>> pendingPostsByThread = new HashMap<>();

        // Iterate through the postListByThreadIdAndStatus map to find all pending posts (status = 0)
        for (Map.Entry<Long, HashMap<Integer, LinkedList<Long>>> threadEntry : cache.getPostListByThreadIdAndStatus().entrySet()) {
            Long threadId = threadEntry.getKey();
            HashMap<Integer, LinkedList<Long>> postsByStatus = threadEntry.getValue();

            LinkedList<Long> pendingPostsInThread = postsByStatus.get(0); // 0 represents PENDING status

            if (pendingPostsInThread != null) {
                HashMap<Integer, LinkedList<Long>> pendingStatusMap = new HashMap<>();
                pendingStatusMap.put(0, new LinkedList<>(pendingPostsInThread)); // Add only pending posts

                pendingPostsByThread.put(threadId, pendingStatusMap);
            }
        }

        return pendingPostsByThread;
    }




}
