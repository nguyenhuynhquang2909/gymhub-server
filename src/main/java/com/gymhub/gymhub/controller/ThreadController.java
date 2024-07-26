package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.domain.miscellaneous.IncreDecre;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Thread Handlers", description = "Handlers for thread-related requests")
@RestController
@RequestMapping("/api/thread")
public class ThreadController {

    @Operation(
            description = "This method returns the top 10 threads ordered by post recency",
            tags = "Homepage"

    )
    @GetMapping("/{trending}")
    public List<Thread> getTrendingThread(){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This method returns the top 10 most viewed threads",
            tags = "Homepage"
    )
    @GetMapping("/most_viewed")
    public List<Thread> getMostViewedThread(

    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This method returns the top 10 most liked threads",
            tags = "Homepage"
    )
    @GetMapping("/most_liked")
    public List<Thread> getMostLikedThread(

    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"flexing\" category",
            tags = {"Homepage", "Flex-Thread Page"}
    )
    @GetMapping("/flexing")
    public List<Thread> getFlexingThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"advise\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/advises")
    public List<Thread> getAdvisesThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"supplement\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/supplement")
    public List<Thread> getSupplementThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }
    @Operation(
            description = "This operation returns a number of threads that belong to the a user",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/user-{id}")
    public List<Thread> getUserThread(
            @Parameter (description = "Id of the user", required = true)
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page

    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This operation creates a new thread",
            tags = ""
    )
    @PostMapping("/new/user-{id}")
    public ResponseEntity<Void> createNewThread(
            @RequestBody Thread thread,
            @Parameter(description = "Id of the user who is creating the new thread", required = true)
            @PathVariable Long id){
        return  new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "This operation increment or decrement the like count of a thread",
            tags = "Thread Containers"
    )
    @PatchMapping("/like/thread-{threadId}/user - {userId}")
    public ResponseEntity<Void> changingLikesForThread(
            @Parameter (description = "Id of the new thread whose like count is to be changed", required = true)
            @PathVariable Long threadId,
            @Parameter (description = "Id of the user who likes or undoes their like" )
            @PathVariable Long userId,
            @RequestBody IncreDecre body){
        return  new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description= "This operation reports a thread to the server and return a string indicating the reason",
            tags = "Thread Containers"
    )
    @PatchMapping("/report/thread-{id}")
    public ResponseEntity<Void> reportThread(@RequestBody String reason, @PathVariable Long id){
        return  new ResponseEntity<>(HttpStatus.OK);
    }








}
