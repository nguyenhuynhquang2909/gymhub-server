package com.gymhub.gymhub.helper;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
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
