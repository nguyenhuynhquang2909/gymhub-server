package com.gymhub.gymhub.helper;

import org.springframework.stereotype.Component;

@Component
public class ThreadSequence {
    private Long threadId = 1L;

    public synchronized boolean incrementing(){
        threadId++;
        return true;
    }

    public Long getUserId() {
        incrementing();
        return threadId--;
    }
}
