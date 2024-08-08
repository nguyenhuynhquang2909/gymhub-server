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
    public ResponseEntity<HashMap<String, List<ThreadResponseDTO>>> getTrendingThread() {
        HashMap<String, List<ThreadResponseDTO>> map = threadService.get10SuggestedThreads();
        System.out.println("Map" + map);
        return ResponseEntity.ok(map);
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"flexing\" category",
            tags = {"Homepage", "Flex-Thread Page"}
    )
    @GetMapping("/flexing")
    public ResponseEntity<List<ThreadResponseDTO>> getFlexingThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(threadService.getAllThreadsByCategory("flexing"));
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"advise\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/advises")
    public ResponseEntity<List<ThreadResponseDTO>> getAdvisesThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(threadService.getAllThreadsByCategory("advices"));
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the \"supplement\" category",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/supplement")
    public ResponseEntity<List<ThreadResponseDTO>> getSupplementThread(
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(threadService.getAllThreadsByCategory("supplement"));
    }

    @Operation(
            description = "This operation returns a number of threads that belong to a user",
            tags = {"Homepage", "Advise-Thread Page"}
    )
    @GetMapping("/user-{id}")
    public ResponseEntity<List<ThreadResponseDTO>> getUserThread(
            @Parameter(description = "Id of the user", required = true)
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        return ResponseEntity.ok(threadService.getAllThreadByOwnerId(id));
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
            description = "This operation reports a thread to the server and return a string indicating the reason",
            tags = "Thread Containers"
    )
    @PatchMapping("/report")
    public ResponseEntity<Void> reportThread(@RequestBody ReportRequestDTO reportRequestDTO){
        return  new ResponseEntity<>(HttpStatus.OK);

}

}

