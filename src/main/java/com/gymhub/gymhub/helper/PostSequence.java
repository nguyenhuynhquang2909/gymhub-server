package com.gymhub.gymhub.helper;

import org.springframework.stereotype.Component;

@Component
public class PostSequence {
    private Long postId;

    public synchronized boolean incrementing(){
        postId++;
        return true;
    }

    public Long getUserId() {
        incrementing();
        return postId--;
    }
}
