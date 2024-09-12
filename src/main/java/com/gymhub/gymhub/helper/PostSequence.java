package com.gymhub.gymhub.helper;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
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
