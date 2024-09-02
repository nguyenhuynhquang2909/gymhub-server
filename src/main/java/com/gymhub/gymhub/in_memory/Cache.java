package com.gymhub.gymhub.in_memory;

import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;

import static com.gymhub.gymhub.helper.HelperMethod.convertStringToxicStatusToBooleanValue;

/**
 * The type Cache.
 */
@Getter
@NoArgsConstructor
@Component
public class Cache {

    /**
     * A map storing all thread IDs.
     */
    HashMap<Long, Long> allThreadID = new HashMap<>();

    /**
     * A map storing all post IDs.
     */
    HashMap<Long, Long> allPostId = new HashMap<>();

    /**
     * A map storing all member IDs.
     */
    HashMap<Long, Long> allMemberID = new HashMap<>();


    /**
     * A map containing the ids of banned user and Ban Info Object (include the banUntilDate and Reason)
     */
    HashMap<Long, BanInfo> bannedList = new HashMap<>();

    ConcurrentHashMap<Long, Set<Long>> followingCache = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, Set<Long>> followersCache = new ConcurrentHashMap<>();

    /**
     * A Map containing session id and all the threads the user views in the session
     */
    HashMap<String, ConcurrentHashMap<Long, Long>> sessions = new HashMap<>();





    /**
     * A map storing parameters for all threads, keyed by thread ID.
     * Each thread's parameter map contains:
     * - "LikeCount" (Integer): The number of likes the thread has received.
     * - "ViewCount" (Integer): The number of views the thread has received.
     * - "PostCount" (Integer): The number of posts in the thread.
     * - "CreationDate" (Long): The creation date of the thread (in milliseconds).
     * - "PostCreationDate" (Long): The creation date of the latest post (in millisecond).
     * - "toxicStatus" (Integer): The toxic - status of the thread (e.g., 1 for non-toxic, 0 for pending, -1 for toxic ).
     * - "resolveStatus" (Integer): Check if thread has been resolved by mod (e.g., 1 for true-resolved, 0 for false-notResolved).
     * "reason": String: The explanation for decision made by AI (ban or not ban), or mod (ban or not ban), or member (report)
     */
    // Modify the map to store any type of value (Number or String)
    LinkedHashMap<Long, ConcurrentHashMap<String, Object>> parametersForAllThreads = new LinkedHashMap<>();



    /**
     * The Parameters for all posts.
     *      * A map storing parameters for all posts, keyed by post ID.
     *      * Each post's parameter map contains:
     *      * - "LikeCount" (Integer): The number of likes the post has received.
     *      * - "CreationDate" (Long): The creation date of the post (in milliseconds).
     *      *  * - "toxicStatus" (Integer): The toxic - status of the thread (e.g., 1 for non-toxic, 0 for pending, -1 for toxic ).
     *      *      * - "resolveStatus" (Integer): Check if thread has been resolved by mod (e.g., 1 for true-resolved, 0 for false-notResolved).
     *      *     * "reason": String: The explanation for decision made by AI (ban or not ban), or mod (ban or not ban), or member (report)
     *
     */
    // Modify the map to store any type of value (Number or String)
    HashMap<Long, ConcurrentHashMap<String, Object>> parametersForAllPosts = new LinkedHashMap<>();


    /**
     * The Thread list by category and toxic status.
     *  * A map categorizing threads by category and status, with lists of thread IDs.
     * //     * Store threads with 3 type of status :
     * //     * 1 for non-toxic,
     * //     * 0 for pending,
     * //     * -1 for toxic
     */
    HashMap<ThreadCategoryEnum, HashMap<Integer, LinkedList<Long>>> threadListByCategoryAndToxicStatus = new HashMap<>();


    /**
     * A map categorizing posts by thread ID,  and status, with lists of post IDs.
     * ex: Thread 116, LinkedList: Status -1 : Post 5, post 6, post 7
     * =>  Store posts with 3 type of  status = 1 for non-toxic, 0 for pending, -1 for toxic
     */
    HashMap<Long, HashMap<Integer, LinkedList<Long>>> postListByThreadIdAndToxicStatus = new HashMap<>();

    /**
     * A map tracking the posts liked by each user, keyed by user ID.
     */
    HashMap<Long, Set<Long>> postLikeListByUser = new HashMap<>();

    /**
     * A map tracking the threads liked by each user, keyed by user ID.
     */
    HashMap<Long, Set<Long>> threadLikeListByUser = new HashMap<>();

    /**
     * A map tracking the posts created by each user, keyed by user ID.
     */
    HashMap<Long, Set<Long>> postListByUser = new HashMap<>();

    /**
     * A map tracking the threads created by each user, keyed by user ID.
     */
    HashMap<Long, Set<Long>> threadListByUser = new HashMap<>();






    //delete queue if LinedList of -1 is larger than 20 (check size every time adding element to that linked list
}