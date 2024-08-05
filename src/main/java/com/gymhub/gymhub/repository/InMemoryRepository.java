package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.actions.AddThreadAction;
import com.gymhub.gymhub.actions.AddUserAction;
import com.gymhub.gymhub.actions.ChangeThreadStatusAction;
import com.gymhub.gymhub.actions.LikeThreadAction;
import com.gymhub.gymhub.actions.MustLogAction;
import com.gymhub.gymhub.actions.ViewThreadAction;
import com.gymhub.gymhub.in_memory.Cache;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.glassfish.jaxb.core.v2.TODO;
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

    private static final String LOG_FILE_PATH = "logs/cache_actions.log";
    private static long actionIdCounter = 0;

    private void logAction(MustLogAction action) {
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
    public boolean addThreadToCache(long threadId, String category, int status, long userId) {
        boolean result = cache.addThreadToCache(threadId, category, status, userId);
        AddThreadAction action = new AddThreadAction(++actionIdCounter, threadId, category, status, userId);
        logAction(action);
        return result;
    }
    public boolean likeThread(long threadId, long userId, int mode) {
        boolean result = cache.likeThread(threadId, userId, mode);
        LikeThreadAction action = new LikeThreadAction(++actionIdCounter, LOG_FILE_PATH, threadId, userId, mode);
        logAction(action);
        return result;
    }
    public boolean viewThread(long threadId) {
        boolean result = cache.viewThread(threadId);
        ViewThreadAction action = new ViewThreadAction(++actionIdCounter, threadId);
        logAction(action);
        return result;
    }
    public boolean ChangeThreadStatus(long threadId, String category, int from, int to) {
        boolean result = cache.changeThreadStatus(threadId, category, from, to);
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(++actionIdCounter, threadId, category, from, to);
        logAction(action);
        return result;
    }
    




}
