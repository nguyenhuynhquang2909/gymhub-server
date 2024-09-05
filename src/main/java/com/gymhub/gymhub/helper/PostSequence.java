package com.gymhub.gymhub.helper;

import org.springframework.stereotype.Component;

@Component
public class PostSequence {
    private Long postId = 1L;

    public synchronized boolean incrementing(){
        postId++;
        return true;
    }

    public Long getUserId() {
        incrementing();
        return postId--;
    }
    public Long getNextPostId() {
        incrementing();
        return postId;
    }
}
