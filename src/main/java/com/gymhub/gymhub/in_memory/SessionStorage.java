package com.gymhub.gymhub.in_memory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
@Component
public class SessionStorage {

    @Autowired
    Cache cache;

    /**
     * This Map tracks the session ID of each User Id. Everytime the user logs in
     * a new session id will be created to replace the old one
     */
    private ConcurrentHashMap<Long, LinkedList<UUID>> usersSessions = new ConcurrentHashMap<>();
    /**
     * This Map tracks session ID and all the threads that have been viewed in the session.
     * Everytime the user logs in, the corresponding key value pair will get replaced by new ones
     */
    private ConcurrentHashMap<UUID, ConcurrentHashMap<Long, Long>> sessionThreadViews = new ConcurrentHashMap<>();

    /**
     * This method creates a session ID and a map to track thread viewing activity of the user
     * It receives the id of the user as a parameter, which will be used as a key for which the session ID
     * will be the value. The Session ID will be the key in the sessionThreadViews map and the
     * thread viewing concurrent map will be the value. Old key-value pairs will get replaced everytime
     * users logs in. Call this method during log in if the session key of the request header is empty
     * This method is also called when the session expires while users are still logged in
     * @param userId
     * @return the identifier of the newly created session
     */
    public UUID createNewSession(Long userId){
        UUID sessionId = UUID.randomUUID();
        ConcurrentHashMap<Long, Long> threadViews = new ConcurrentHashMap<>();

        //check if this is the first time the user logs in
        if (!usersSessions.containsKey(userId)) {
            //Initiate a list to contain Ids of all sessions belonging to the user
            LinkedList<UUID> sessions = new LinkedList<>();
            //Put the newly created session ID in the list
            sessions.add(sessionId);
            usersSessions.put(userId, sessions);
        }
        else {
            usersSessions.get(userId).add(sessionId);
        }
        return sessionId;
    }


    /**
     * Call this method to create a new session when the session key in the header is empty
     * @return the identifier of the newly created session
     */
    public UUID createNewSessionWhenViewThread(){
        UUID sessionId = UUID.randomUUID();
        ConcurrentHashMap<Long, Long> threadViews = new ConcurrentHashMap<>();
        sessionThreadViews.put(sessionId, threadViews);
        return sessionId;
    }



    /**
     * This method will be called when the user clicks on a thread.
     * If the thread ID does not exist in the sessionThreadViews map,
     * a key value pair will be created, and the View Count parameter will be
     * incremented by one. If the thread already exists, it will calculate
     * the elapsed time between the last view. If the elapsed time is in excess of
     * 5 minutes, it will increment the view count of the thread by 1
     * @param sessionId
     * @param threadId
     * @return
     */
    public boolean addThreadToThreadView(UUID sessionId, Long threadId){
        sessionThreadViews.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
        if (!sessionThreadViews.get(sessionId).containsKey(threadId)){
            sessionThreadViews.get(sessionId).put(threadId, System.currentTimeMillis());
            int currentViews = (int) cache.getParametersForAllThreads().get(threadId).get("ViewCount");
            cache.parametersForAllThreads.get(threadId).put("ViewCount", currentViews + 1);
        }
        else {
            if (sessionThreadViews.get(sessionId).get(threadId) - System.currentTimeMillis() >= 300 * 1000){
                sessionThreadViews.get(sessionId).put(threadId, System.currentTimeMillis());
                int currentViews = (int) cache.getParametersForAllThreads().get(threadId).get("ViewCount");
                cache.parametersForAllThreads.get(threadId).put("ViewCount", currentViews + 1);

            }
        }
        return true;
    }


}
