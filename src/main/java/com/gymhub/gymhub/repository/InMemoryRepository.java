package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.actions.*;
import com.gymhub.gymhub.in_memory.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.SubmissionPublisher;

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

    // Add User
    public boolean addUser(long userId) {
        boolean result = cache.addUser(userId);
        AddUserAction action = new AddUserAction(++actionIdCounter, userId);
        logAction(action);
        return result;
    }

    // Add Thread
    public boolean addThreadToCache(long threadId, String category, String toxicStatus, long userId, boolean resolveStatus) {
        boolean result = cache.addThreadToCache(threadId, category, toxicStatus, userId, resolveStatus);
        AddThreadAction action = new AddThreadAction(++actionIdCounter, threadId, category, toxicStatus, userId, resolveStatus);
        logAction(action);
        return result;
    }

    // Change Thread Status for Reporting and Complaining
    public boolean changeThreadStatusForReportingAndComplaining(long threadId, String category, int from, int to, String reason) {
        boolean result = cache.changeThreadStatusForReportingAndComplaining(threadId, category, from, to, reason);
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(++actionIdCounter, threadId, category, from, to, reason);
        logAction(action);
        return result;
    }

//    // Change Thread Status from Mod Dashboard
//    public boolean changeThreadStatusFromModDashBoard(long threadId, String newStatus, String reason) {
//        boolean result = cache.changeThreadStatusFromModDashBoard(threadId, newStatus, reason);
//        ChangeThreadStatusAction action = new ChangeThreadStatusAction(++actionIdCounter, threadId, newStatus, reason);
//        logAction(action);
//        return result;
//    }

    // Change Post Status
    public boolean changePostStatus(long postId, long threadId, int from, int to, String reason) {
        boolean result = cache.changePostStatus(postId, threadId, from, to, reason);
        ChangePostStatusAction action = new ChangePostStatusAction(++actionIdCounter, postId, threadId, from, to, reason);
        logAction(action);
        return result;
    }

    // Add Post
    public boolean addPostToCache(long postId, long threadId, long userId, String toxicStatus, boolean resolveStatus) {
        boolean result = cache.addPostToCache(threadId, postId, userId, toxicStatus, resolveStatus);
        AddPostAction action = new AddPostAction(++actionIdCounter, threadId, postId, userId, toxicStatus, resolveStatus);
        logAction(action);
        return result;
    }

    // Like Post
    public boolean likePost(long postId, long userId, long threadId, int mode) {
        boolean result = cache.likePost(postId, userId, threadId, mode);
        LikePostAction action = new LikePostAction(++actionIdCounter, postId, userId, threadId, mode);
        logAction(action);
        return result;
    }

    // Suggested Threads
    public HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> getSuggestedThreads() {
        return cache.getSuggestedThreads();
    }

    // Return Thread by Category
    public boolean returnThreadByCategory(String category, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher) {
        return cache.returnThreadByCategory(category, limit, offset, publisher);
    }

    // Return Post by Thread ID
    public boolean returnPostByThreadId(long threadId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher, Long userId) {
        return cache.returnPostByThreadId(threadId, limit, offset, publisher, userId);
    }

    // Check Ban
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
                        cache.addThreadToCache(addThreadAction.getThreadId(), addThreadAction.getCategory(), addThreadAction.getToxicStatus(), addThreadAction.getUserId(), addThreadAction.isResolveStatus());
                    } else if (action instanceof ChangeThreadStatusAction) {
                        ChangeThreadStatusAction changeThreadStatusAction = (ChangeThreadStatusAction) action;
                        cache.changeThreadStatusForReportingAndComplaining(changeThreadStatusAction.getThreadId(), changeThreadStatusAction.getCategory(), changeThreadStatusAction.getFrom(), changeThreadStatusAction.getTo(), changeThreadStatusAction.getReason());
                    } else if (action instanceof ChangePostStatusAction) {
                        ChangePostStatusAction changePostStatusAction = (ChangePostStatusAction) action;
                        cache.changePostStatus(changePostStatusAction.getPostId(), changePostStatusAction.getThreadId(), changePostStatusAction.getFrom(), changePostStatusAction.getTo(), changePostStatusAction.getReason());
                    } else if (action instanceof AddPostAction) {
                        AddPostAction addPostAction = (AddPostAction) action;
                        cache.addPostToCache(addPostAction.getThreadId(), addPostAction.getPostId(), addPostAction.getUserId(), addPostAction.getToxicStatus(), addPostAction.isResolveStatus());
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


    // Get Thread List by Category and Status
    public HashMap<Integer, LinkedList<Long>> getThreadListByCategoryAndStatus(String category) {
        return cache.getThreadListByCategoryAndStatus().getOrDefault(category, new HashMap<>());
    }

    // Get Pending Threads
    public HashMap<String, HashMap<Integer, LinkedList<Long>>> getPendingThreads() {
        HashMap<String, HashMap<Integer, LinkedList<Long>>> pendingThreadsByCategory = new HashMap<>();

        for (Map.Entry<String, HashMap<Integer, LinkedList<Long>>> categoryEntry : cache.getThreadListByCategoryAndStatus().entrySet()) {
            String category = categoryEntry.getKey();
            HashMap<Integer, LinkedList<Long>> threadsByStatus = categoryEntry.getValue();

            LinkedList<Long> pendingThreadsInCategory = threadsByStatus.get(0);

            if (pendingThreadsInCategory != null) {
                HashMap<Integer, LinkedList<Long>> pendingStatusMap = new HashMap<>();
                pendingStatusMap.put(0, new LinkedList<>(pendingThreadsInCategory));

                pendingThreadsByCategory.put(category, pendingStatusMap);
            }
        }

        return pendingThreadsByCategory;
    }

    // Get Pending Posts
    public HashMap<Long, HashMap<Integer, LinkedList<Long>>> getPendingPosts() {
        HashMap<Long, HashMap<Integer, LinkedList<Long>>> pendingPostsByThread = new HashMap<>();

        for (Map.Entry<Long, HashMap<Integer, LinkedList<Long>>> threadEntry : cache.getPostListByThreadIdAndStatus().entrySet()) {
            Long threadId = threadEntry.getKey();
            HashMap<Integer, LinkedList<Long>> postsByStatus = threadEntry.getValue();

            LinkedList<Long> pendingPostsInThread = postsByStatus.get(0);

            if (pendingPostsInThread != null) {
                HashMap<Integer, LinkedList<Long>> pendingStatusMap = new HashMap<>();
                pendingStatusMap.put(0, new LinkedList<>(pendingPostsInThread));

                pendingPostsByThread.put(threadId, pendingStatusMap);
            }
        }

        return pendingPostsByThread;
    }
}
