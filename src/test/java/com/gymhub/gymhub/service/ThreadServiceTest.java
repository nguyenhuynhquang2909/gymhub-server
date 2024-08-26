package com.gymhub.gymhub.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadServiceTest {

//    @Test
//    void get10SuggestedThreads() {
//        ThreadService threadService = new ThreadService();
//        threadService.get10SuggestedThreads();
//    }
    @Test
    void getTheadByOwnerId() throws Exception {
        ThreadService threadService = new ThreadService();
        threadService.getAllThreadByOwnerId(1L,5, 1);
    }
}