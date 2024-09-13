package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.config.CustomUserDetails;
import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.dto.*;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import com.gymhub.gymhub.service.ThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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





    @Operation(description = "This operation returns a thread by its id", tags = "Thread Request Handlers, Thread Page")
    @GetMapping("/{id}")
    public ResponseEntity<ThreadResponseDTO> getThreadByThreadId(@PathVariable("id") Long id) {
        try {
            ThreadResponseDTO threadResponseDTO = threadService.getAThreadByThreadId(id);
            return ResponseEntity.ok(threadResponseDTO); // 200 OK
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }



    @Operation(description = "This method returns all the thread ordered by relevant/trending score and top 10 threads ordered by the creation date of the latest post", tags = "Homepage")
    @GetMapping("/suggested")
    public ResponseEntity<HashMap<String, List<ThreadResponseDTO>>> getSuggestedThreads() {
        try {
            HashMap<String, List<ThreadResponseDTO>> map = threadService.get10SuggestedThreads();
            return ResponseEntity.ok(map); // 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns a number of threads that belong to the 'flexing' category", tags = {"Homepage", "Flexing Category"})
    @GetMapping("/flexing")
    public ResponseEntity<List<ThreadResponseDTO>> getFlexingThread(
            @Parameter(description = "The number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        try {
            int offset = page * limit;
            return ResponseEntity.ok(threadService.getAllThreadsByCategory(ThreadCategoryEnum.FLEXING, limit, offset)); // 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns a number of threads that belong to the 'advice' category", tags = {"Homepage", "Advice Category"})
    @GetMapping("/advice")
    public ResponseEntity<List<ThreadResponseDTO>> getAdviceThread(
            @Parameter(description = "The number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        try {
            int offset = page * limit;
            return ResponseEntity.ok(threadService.getAllThreadsByCategory(ThreadCategoryEnum.ADVICE, limit, offset)); // 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns a number of threads that belong to the 'supplement' category", tags = {"Homepage", "Supplement Category"})
    @GetMapping("/supplement")
    public ResponseEntity<List<ThreadResponseDTO>> getSupplementThread(
            @Parameter(description = "The number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        try {
            int offset = page * limit;
            return ResponseEntity.ok(threadService.getAllThreadsByCategory(ThreadCategoryEnum.SUPPLEMENT, limit, offset)); // 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation returns a number of threads that belong to a member", tags = {"Member Profile Page"})
    @GetMapping("/user-{id}")
    public ResponseEntity<List<ThreadResponseDTO>> getUserThread(
            @Parameter(description = "Id of the user", required = true)
            @PathVariable Long id,
            @Parameter(description = "the number of threads to be returned in a single fetch", required = false)
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @Parameter(description = "The next page to be fetched", required = false)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        try {
            return ResponseEntity.ok(threadService.getAllThreadByOwnerId(id, limit, page)); // 200 OK
        } catch (Exception e) {
           e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @Operation(summary = "Create a new thread", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/new")
    public ResponseEntity<ToxicStatusEnum> createNewThread(
            @RequestBody ThreadRequestDTO threadRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            // Log the initial size of the thread repository
            System.out.println("Before creating thread: Total threads = " + threadRepository.findAll().size());

            // Create a new thread
            ToxicStatusEnum statusEnum = threadService.createThread(userDetails.getId(), threadRequest);

            // Log the size of the thread repository after creating the new thread
            System.out.println("After creating thread: Total threads = " + threadRepository.findAll().size());

            return new ResponseEntity<>(statusEnum, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "This operation reports a thread to the server and returns a boolean indicating success", tags = "Thread Page")
    @PatchMapping("/report")
    public ResponseEntity<String> reportThread(
            @RequestBody ThreadRequestDTO threadRequestDTO,
            @RequestParam("reason") String reason) {
        try {
            boolean success = threadService.reportThread(threadRequestDTO, reason);
            if (success) {
                return ResponseEntity.ok("Thread reported successfully."); // 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report thread."); // 400 Bad Request
            }
        } catch (Exception e) {
           e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to report thread due to server error."); // 500 Internal Server Error
        }
    }

    @Operation(description = "This operation updates the thread title by finding thread ID (checks if the member is the thread owner)", tags = "Thread Page")
    @PatchMapping("/update/{threadID}")
    public ResponseEntity<ToxicStatusEnum> updateThread(
            @Parameter(description = "The ID of the thread to be updated", required = true)
            @PathVariable Long threadID,
            @RequestBody ThreadRequestDTO threadRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            // Set the threadID in the DTO
            threadRequestDTO.setId(threadID);
            ToxicStatusEnum statusEnum = threadService.updateThread(userDetails.getId(), threadRequestDTO);
            return new ResponseEntity<>(statusEnum, HttpStatus.OK);

        } catch (Exception e) {
            // Print out the error message to the console
            e.printStackTrace();
            // Return a generic internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }


}
