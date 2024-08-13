package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.ReportThreadRequestDTO;
import com.gymhub.gymhub.dto.ThreadRequestDTO;
import com.gymhub.gymhub.dto.ThreadResponseDTO;
import com.gymhub.gymhub.dto.UpdateThreadTitleDTO;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import com.gymhub.gymhub.service.ThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Tag(name = "Thread Request Handlers", description = "Handlers for thread-related requests")
@RestController
@RequestMapping("/thread")
public class ThreadController {

    @Autowired
    private ThreadService threadService;
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private MemberRepository memberRepository;

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
            description = "This operation returns a number of threads that belong to the 'flexing' category",
            tags = {"Homepage", "Flexing Category"}
    )
    @GetMapping("/flexing")
    public ResponseEntity<List<ThreadResponseDTO>> getFlexingThread(
            @Parameter(description = "The number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        int offset = page * limit; // Calculate the offset based on page and limit
        return ResponseEntity.ok(threadService.getAllThreadsByCategory("flexing", limit, offset));
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the 'advice' category",
            tags = {"Homepage", "Advice Category"}
    )
    @GetMapping("/advise")
    public ResponseEntity<List<ThreadResponseDTO>> getAdviseThread(
            @Parameter(description = "The number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        int offset = page * limit; // Calculate the offset based on page and limit
        return ResponseEntity.ok(threadService.getAllThreadsByCategory("advice", limit, offset));
    }

    @Operation(
            description = "This operation returns a number of threads that belong to the 'supplement' category",
            tags = {"Homepage", "Supplement Category"}
    )
    @GetMapping("/supplement")
    public ResponseEntity<List<ThreadResponseDTO>> getSupplementThread(
            @Parameter(description = "The number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        int offset = page * limit; // Calculate the offset based on page and limit
        return ResponseEntity.ok(threadService.getAllThreadsByCategory("supplement", limit, offset));
    }

    @Operation(
            description = "This operation returns a number of threads that belong to a member",
            tags = {"Member Profile Page"}
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
      /*  return ResponseEntity.ok(threadService.getAllThreadByOwnerId(id));*/
        return ResponseEntity.ok(threadService.getAllThreadByOwnerId(id, limit, page));
    }

    @Operation(
            description = "This operation creates a new thread",
            tags = "Homepage"
    )

    @PostMapping("/new")
    public ResponseEntity<String> createNewThread(
            @RequestBody ThreadRequestDTO threadRequest,
            @AuthenticationPrincipal UserDetails userDetails
            ){
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String username = userDetails.getUsername();
        Member member = memberRepository.findMemberByUserName(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        threadRequest.setAuthorId(member.getId());
        System.out.println(threadRepository.findAll().size());
        threadService.createThread(threadRequest);        //New API this line
        System.out.println(threadRepository.findAll().size());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(
            description = "This operation reports a thread to the server and returns a boolean indicating success",
            tags = "Thread Page"
    )
    @PatchMapping("/report")
    public ResponseEntity<String> reportThread(@RequestBody ReportThreadRequestDTO reportThreadRequestDTO) {

        // Call the service method to report the thread
        boolean success = threadService.reportThread(reportThreadRequestDTO);

        // Return appropriate response based on success or failure
        if (success) {
            return new ResponseEntity<>("Thread reported successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to report thread.", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            description = "This operation updates the thread title by finding thread ID (checks if the member is the thread owner)",
            tags = "Thread Page"
    )
    @PatchMapping("/update/{threadID}")
    public ResponseEntity<ThreadResponseDTO> updateThreadTitle(
            @Parameter(description = "The ID of the thread to be updated", required = true)
            @PathVariable Long threadID,
            @RequestBody UpdateThreadTitleDTO updateThreadTitleDTO) {

        // Set the threadID in the DTO or pass it directly to the service method
        updateThreadTitleDTO.setThreadId(threadID);

        return threadService.updateThread(updateThreadTitleDTO);
    }

    //create private conversation (private thread) between 2 member



    //view private conversation (private thread) between 2 member



}

