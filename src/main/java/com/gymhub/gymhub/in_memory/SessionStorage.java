package com.gymhub.gymhub.in_memory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
@Component
public class SessionStorage {

    @Autowired
    Cache cache;

    private ConcurrentHashMap<Long, UUID> usersSessions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, ConcurrentHashMap<Long, Long>> sessionThreadViews = new ConcurrentHashMap<>();

    public boolean createNewSession(Long userId){
        UUID sessionId = UUID.randomUUID();
        ConcurrentHashMap<Long, Long> threadViews = new ConcurrentHashMap<>();
        usersSessions.put(userId, sessionId);
        sessionThreadViews.put(sessionId, threadViews);
        return true;
    }

    public boolean addThreadToThreadView(UUID sessionId, Long threadId){
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
