package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.domain.miscellaneous.IncreDecre;
import com.gymhub.gymhub.domain.miscellaneous.ListOfPostsPaginationJson;
import com.gymhub.gymhub.domain.miscellaneous.ListOfThreadsPaginationJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Api(value = "Handlers for thread-related requests")
@RestController
@RequestMapping("/api/thread")
public class ThreadController {

    @ApiOperation(
            value = "This method returns the top 10 most trending threads",
            notes = "Trending is measured by the date of the latest post",
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

    @ApiOperation(
            value = "This method returns the top 10 most viewed threads",
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

    @ApiOperation(
            value = "This method returns the top 10 most liked threads",
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

    @ApiOperation(
            value = "This operation returns a number of threads that belong to the \"flexing\" category",
            tags = {"Homepage", "Flex-Thread Page"}
    )
    @GetMapping("/flexing")
    public ListOfThreadsPaginationJson getFlexingThread(
            @ApiParam(value = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "id of the next thread id from which the server will fetch", required = false)
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        ListOfThreadsPaginationJson json = new ListOfThreadsPaginationJson();
        json.setThreads(threads);
        json.setAfter(after);
        return json;
    }

    @ApiOperation(
            value = "This operation returns a number of threads that belong to the \"advise\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/advises")
    public ListOfThreadsPaginationJson getAdvisesThread(
            @ApiParam(value = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "id of the next thread id from which the server will fetch", required = false)
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        ListOfThreadsPaginationJson json = new ListOfThreadsPaginationJson();
        json.setThreads(threads);
        json.setAfter(after);
        return json;
    }

    @ApiOperation(
            value = "This operation returns a number of threads that belong to the \"supplement\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/supplement")
    public ListOfThreadsPaginationJson getSupplementThread(
            @ApiParam(value = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "id of the next thread id from which the server will fetch", required = false)
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after
    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        ListOfThreadsPaginationJson json = new ListOfThreadsPaginationJson();
        json.setThreads(threads);
        json.setAfter(after);
        return json;
    }
    @ApiOperation(
            value = "This operation returns a number of threads that belong to the a user",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/user-{id}")
    public ListOfThreadsPaginationJson getUserThread(
            @ApiParam (value = "Id of the user", required = true)
            @PathVariable Long id,
            @ApiParam(value = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @ApiParam(value = "id of the next thread id from which the server will fetch", required = false)
            @RequestParam(value = "after", required = false, defaultValue = "0") Integer after

    ){
        List<Thread> threads = new ArrayList<>();
        Thread thread1 = new Thread();
        Thread thread2 = new Thread();
        threads.add(thread1);
        threads.add(thread2);
        ListOfThreadsPaginationJson json = new ListOfThreadsPaginationJson();
        json.setThreads(threads);
        json.setAfter(after);
        return json;
    }

    @ApiOperation(
            value = "This operation creates a new thread",
            tags = ""
    )
    @PostMapping("/new/user-{id}")
    public ResponseEntity<Void> createNewThread(
            @RequestBody Thread thread,
            @ApiParam(value = "Id of the user who is creating the new thread", required = true)
            @PathVariable Long id){
        return  new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(
            value = "This operation increment or decrement the like count of a thread",
            tags = "Thread Containers"
    )
    @PatchMapping("/like/thread-{threadId}/user - {userId}")
    public ResponseEntity<Void> changingLikesForThread(
            @ApiParam (value = "Id of the new thread whose like count is to be changed", required = true)
            @PathVariable Long threadId,
            @ApiParam (value = "Id of the user who likes or undoes their like" )
            @PathVariable Long userId,
            @RequestBody IncreDecre body){
        return  new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(
            value = "This operation reports a thread to the server and return a string indicating the reason",
            tags = "Thread Containers"
    )
    @PatchMapping("/report/thread-{id}")
    public ResponseEntity<Void> reportThread(@RequestBody String reason, @PathVariable Long id){
        return  new ResponseEntity<>(HttpStatus.OK);
    }








}
