package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Thread;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/thread")
public class ThreadController {

    @GetMapping("/{trending}")
    public List<Thread> getTrendingThread(){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @GetMapping("/most_viewed")
    public List<Thread> getMostViewedThread(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        return null;
    }

    @GetMapping("/most_liked")
    public List<Thread> getMostLikedThread(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        return null;
    }

    @GetMapping("/flexing")
    public List<Thread> getFlexingThread(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        return null;
    }

    @GetMapping("/advises")
    public List<Thread> getAdvisesThread(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        return null;
    }

    @GetMapping("/supplement")
    public List<Thread> getSupplementThread(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        return null;
    }

    @GetMapping("/user-{id}")
    public List<Thread> getUserThread(
            @PathVariable Long id,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        return null;
    }







}
