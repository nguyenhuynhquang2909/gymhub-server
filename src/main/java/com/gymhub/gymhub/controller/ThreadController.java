package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.dto.IncreDecreDTO;
import com.gymhub.gymhub.dto.ReportRequestDTO;
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
import java.util.HashMap;
import java.util.List;

@Tag(name = "Thread Handlers", description = "Handlers for thread-related requests")
@RestController
@RequestMapping("/thread")
public class ThreadController {
    @Autowired
    private ThreadService threadService;

    @Operation(
            description = "This method returns the top 10 threads ordered by relevant/trending score and top 10 threads ordered by the creation date of the latest post",
            tags = "Homepage"

    )
    @GetMapping("/suggested")
    public HashMap<String, List<ThreadResponseDTO>> getTrendingThread(){
        HashMap<String, List<ThreadResponseDTO>> map = new HashMap<>();
        List<ThreadResponseDTO> responseByAlgorithm = new ArrayList<>();
        List<ThreadResponseDTO> responseByPostCreationDate = new ArrayList<>();
        map.put("By Algorithm", responseByAlgorithm);
        map.put("By PostCreationDate", responseByPostCreationDate);
        ThreadResponseDTO thread1 = new ThreadResponseDTO();
        ThreadResponseDTO thread2 = new ThreadResponseDTO();
        responseByAlgorithm.add(thread1);
        responseByPostCreationDate.add(thread2);
        threadService.get10SuggestedThreads(); // API this line
        return map;
    }

    /**
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
     **/



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
    @PostMapping("/new")
    public ResponseEntity<Void> createNewThread(
            @RequestBody ThreadRequestDTO threadRequest){
        threadService.createThread(threadRequest);        //New API this line
        return  new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(
            description= "This operation reports a thread to the server and return a string indicating the reason",
            tags = "Thread Containers"
    )
    @PatchMapping("/report")
    public ResponseEntity<Void> reportThread(@RequestBody ReportRequestDTO reportRequestDTO){
        return  new ResponseEntity<>(HttpStatus.OK);
    }








}
