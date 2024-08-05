package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.IncreDecreDTO;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.dto.ThreadResponseDTO;
import com.gymhub.gymhub.service.ThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Thread Handlers", description = "Handlers for thread-related requests")
@RestController
@RequestMapping("/thread")
public class ThreadController {
    @Autowired
    private ThreadService threadService;

    @Operation(
            description = "This method returns the top 10 threads ordered by relevant/trending score",
            tags = "Homepage"

    )
    @GetMapping("/suggested")
    public List<ThreadResponseDTO> getTrendingThread(){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
        threadService.get10SuggestedThreads(); // API this line
        return threads;
    }

    @Operation(
            description = "This method returns the  top 10 threads ordered by post recency",
            tags = "Homepage"
    )

    @GetMapping("/latest-discussion")
    public List<ThreadResponseDTO> getLatestDiscussionThread(){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
threadService.get10LatestDicussionThreads(); //API this line (currently null)
        return threads;
    }

    @Operation(
            description = "This method returns the top 10 most viewed threads",
            tags = "Homepage"
    )
    @GetMapping("/most_viewed")
    public List<ThreadResponseDTO> getMostViewedThread(

    ){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This method returns the top 10 most liked threads",
            tags = "Homepage"
    )
    @GetMapping("/most_liked")
    public List<ThreadResponseDTO> getMostLikedThread(

    ){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
        return threads;
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"flexing\" category",
            tags = {"Homepage", "Flex-Thread Page"}
    )
    @GetMapping("/flexing")
    public List<ThreadResponseDTO> getFlexingThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
      threadService.getAllThreadsByCategory("flexing"); //API this line

        return threads;
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"advise\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/advises")
    public List<ThreadResponseDTO> getAdvisesThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
        threadService.getAllThreadsByCategory("advices");
        return threads;
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"supplement\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/supplement")
    public List<ThreadResponseDTO> getSupplementThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
        threadService.getAllThreadsByCategory("supplement");
        return threads;
    }
    @Operation(
            description = "This operation returns a number of threads that belong to the a user",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/user-{id}")
    public List<ThreadResponseDTO> getUserThread(
            @Parameter (description = "Id of the user", required = true)
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page

    ){
        List<ThreadResponseDTO> threads = new ArrayList<>();
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        threads.add(thread1);
        threads.add(thread2);
        threadService.getAllThreadByOwnerId(id); //API line here
        return threads;
    }

    @Operation(
            description = "This operation creates a new thread",
            tags = ""
    )
    @PostMapping("/new/user-{id}")
    public ResponseEntity<Void> createNewThread(
            @RequestBody ThreadRequestDTO threadRequest,
            @Parameter(description = "Id of the user who is creating the new thread", required = true)
            @PathVariable Long id){
        threadService.createThread(threadRequest);        //New API this line
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
            @RequestBody IncreDecreDTO body){
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
