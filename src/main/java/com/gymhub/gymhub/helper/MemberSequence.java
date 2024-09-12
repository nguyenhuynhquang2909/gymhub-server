package com.gymhub.gymhub.helper;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
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
