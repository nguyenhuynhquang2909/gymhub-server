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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
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


    public boolean changeThreadStatus(long threadId, String category, int from, int to, String reason) {
        boolean result = cache.changeThreadStatus(threadId, category, from, to, reason);
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(++actionIdCounter, threadId, category, from, to, reason);
        logAction(action);
        return result;
    }
    public boolean changePostStatus(long postId, long threadId, int from, int to, String reason) {
        boolean result = cache.changePostStatus(postId, threadId, from, to, reason);
        ChangePostStatusAction action = new ChangePostStatusAction(++actionIdCounter, postId, threadId, from, to, reason);
        logAction(action);
        return result;
    }

    public boolean addPostToCache(long postId, long threadId, long userId, int status) {
        boolean result = cache.addPostToCache(threadId, postId, userId, status);
        AddPostAction action = new AddPostAction(++actionIdCounter, threadId, postId, userId, status);
        logAction(action);
        return result;
    }
    public boolean likePost(long postId, long userId, long threadId,  int mode) {
        boolean result = cache.likePost(postId, userId, threadId, mode);
        LikePostAction action = new LikePostAction(++actionIdCounter, postId, userId, threadId, mode);
        logAction(action);
        return result;
    }

    public HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> getSuggestedThreads(){
        return cache.getSuggestedThreads();
    }

    public boolean returnThreadByCategory(String category, Long userId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher){
        return cache.returnThreadByCategory(category, userId, limit, offset, publisher);
    }

    public boolean returnPostByThreadId(long threadId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher, Long userId){
        return cache.returnPostByThreadId(threadId, limit, offset, publisher, userId);
    }

    public boolean checkBan(Long userId){
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
                    }

                    else if (action instanceof AddThreadAction) {
                        AddThreadAction addThreadAction = (AddThreadAction) action;
                        cache.addThreadToCache(addThreadAction.getThreadId(), addThreadAction.getCategory(), addThreadAction.getStatus(), addThreadAction.getUserId());
                    }

                    else if (action instanceof ChangeThreadStatusAction) {
                        ChangeThreadStatusAction changeThreadStatusAction = (ChangeThreadStatusAction) action;
                        cache.changeThreadStatus(changeThreadStatusAction.getThreadId(), changeThreadStatusAction.getCategory(), changeThreadStatusAction.getFrom(), changeThreadStatusAction.getTo(), changeThreadStatusAction.getReason());
                    }

                    else if (action instanceof ChangePostStatusAction) {
                        ChangePostStatusAction changePostStatusAction = (ChangePostStatusAction) action;
                        cache.changePostStatus(changePostStatusAction.getPostId(), changePostStatusAction.getThreadId(),changePostStatusAction.getFrom(), changePostStatusAction.getTo(), changePostStatusAction.getReason());
                    }

                    else if (action instanceof AddPostAction) {
                        AddPostAction addPostAction = (AddPostAction) action;
                        cache.addPostToCache(addPostAction.getThreadId(), addPostAction.getPostId(), addPostAction.getUserId(), addPostAction.getStatus());
                    }
                    else if (action instanceof LikePostAction) {
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




}
