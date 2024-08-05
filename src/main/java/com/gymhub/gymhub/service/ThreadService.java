package com.gymhub.gymhub.service;

import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThreadService {
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private Cache cache;

    public void get10SuggestedThreads(){

        System.out.println(    cache.getMostTrendingThreads());
    }


}
