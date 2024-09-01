package com.gymhub.gymhub.helper;

import org.springframework.stereotype.Component;

@Component
public class MemberSequence {
    private Long userId = 1L;

    public synchronized boolean incrementing(){
        userId++;
        return true;
    }

    public Long getUserId() {
        incrementing();
        return userId--;
    }
}
