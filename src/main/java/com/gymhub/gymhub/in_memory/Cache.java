package com.gymhub.gymhub.in_memory;

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
     * Follow another member.
     *
     * @param followerId The ID of the member who wants to follow.
     * @param followingId The ID of the member to be followed.
     */
    public void follow(Long followerId, Long followingId) {
        followingCache.computeIfAbsent(followerId, k -> ConcurrentHashMap.newKeySet()).add(followingId);
        followersCache.computeIfAbsent(followingId, k -> ConcurrentHashMap.newKeySet()).add(followerId);
    }

    /**
     * Unfollow another member.
     *
     * @param followerId The ID of the member who wants to unfollow.
     * @param followingId The ID of the member to be unfollowed.
     */
    public void unfollow(Long followerId, Long followingId) {
        Set<Long> follwingSet = followingCache.get(followerId);
        if (follwingSet != null) {
            follwingSet.remove(followerId);
        }
        Set<Long> followersSet = followersCache.get(followingId);
        if (followersSet != null) {
            followersSet.remove(followerId);
        }
    }

    /**
     * Get the list of followers for a member.
     *
     * @param memberId The ID of the member.
     * @return A set of member IDs who follow the given member.
     */
    public Set<Long> getFollowers(Long memberId) {
        return followersCache.getOrDefault(memberId, ConcurrentHashMap.newKeySet());
    }

    /**
     * Get the list of members a user is following.
     *
     * @param memberId The ID of the member.
     * @return A set of member IDs the given member is following.
     */
    public Set<Long> getFollowing(Long memberId) {
        return followingCache.getOrDefault(memberId, ConcurrentHashMap.newKeySet());
    }

    /**
     * Get the number of followers for a member.
     *
     * @param memberId The ID of the member.
     * @return The number of followers.
     */
    public int getFollowersNumber(Long memberId) {
        Set<Long> followers = followersCache.get(memberId);
        return followers != null ? followers.size() : 0;
    }

    /**
     * Get the number of members a user is following.
     *
     * @param memberId The ID of the member.
     * @return The number of members the user is following.
     */
    public int getFollowingNumber(Long memberId) {
        Set<Long> following = followingCache.get(memberId);
        return following != null ? following.size() : 0;
    }




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


    public ToxicStatusEnum getToxicStatusByThreadId(Long threadId) {
        // Check if the thread exists in the map
        if (parametersForAllThreads.containsKey(threadId)) {
            // Retrieve the parameter map for the thread
            ConcurrentHashMap<String, Object> threadParameters = parametersForAllThreads.get(threadId);
            // Return the toxicStatus if it exists, otherwise return null
            return (ToxicStatusEnum) threadParameters.getOrDefault("toxicStatus", null);
        } else {
            // If the thread ID is not found, return null or throw an exception based on your requirements
            return null;
        }
    }


    // Method to get the resolveStatus of a thread by its ID
    public boolean getResolveStatusByThreadId(Long threadId) {
        // Check if the thread exists in the map
        if (parametersForAllThreads.containsKey(threadId)) {
            // Retrieve the parameter map for the thread
            ConcurrentHashMap<String, Object> threadParameters = parametersForAllThreads.get(threadId);
            // Return the resolveStatus if it exists, otherwise return null
            return (boolean) threadParameters.getOrDefault("resolveStatus", null);
        } else {
            // If the thread ID is not found, return null or throw an exception based on your requirements
            throw new RuntimeException("Thread not found");
        }
    }

    public String getReasonByThreadId(Long threadId) {
        // Check if the thread exists in the map
        if (parametersForAllThreads.containsKey(threadId)) {
            // Retrieve the parameter map for the thread
            ConcurrentHashMap<String, Object> threadParameters = parametersForAllThreads.get(threadId);
            // Return the toxicStatus if it exists, otherwise return null
            return (String) threadParameters.getOrDefault("reason", null);
        } else {
            // If the thread ID is not found, return null or throw an exception based on your requirements
            return null;
        }
    }

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

    // Method to get the resolveStatus of a post by its ID
    public boolean getResolveStatusByPostId(Long postId) {
        // Check if the post exists in the map
        if (parametersForAllPosts.containsKey(postId)) {
            // Retrieve the parameter map for the post
            ConcurrentHashMap<String, Object> postParameters = parametersForAllPosts.get(postId);
            // Return the resolveStatus if it exists, otherwise return null
            return (boolean) postParameters.getOrDefault("resolveStatus", null);
        } else
        {
            throw new RuntimeException("postId " + postId + " not found");
        }
    }

    // Method to get the toxicStatus of a post by its ID
    public ToxicStatusEnum getToxicStatusByPostId(Long postId) {
        // Check if the post exists in the map
        if (parametersForAllPosts.containsKey(postId)) {
            // Retrieve the parameter map for the post
            ConcurrentHashMap<String, Object> postParameters = parametersForAllPosts.get(postId);
            // Return the toxicStatus if it exists, otherwise return null
            return (ToxicStatusEnum) postParameters.getOrDefault("toxicStatus", null);
        } else {
            // If the post ID is not found, return null or throw an exception based on your requirements
            return null;
        }
    }


    public String getReasonByPostId(Long postId) {
        // Check if the post exists in the map
        if (parametersForAllPosts.containsKey(postId)) {
            // Retrieve the parameter map for the post
            ConcurrentHashMap<String, Object> postParameters = parametersForAllPosts.get(postId);
            // Return the resolveStatus if it exists, otherwise return null
            return (String) postParameters.getOrDefault("reason", null);
        } else
        {
            throw new RuntimeException("postId " + postId + " not found");
        }
    }


    /**
     * The Thread list by category and toxic status.
     *  * A map categorizing threads by category and status, with lists of thread IDs.
     * //     * Store threads with 3 type of status :
     * //     * 1 for non-toxic,
     * //     * 0 for pending,
     * //     * -1 for toxic
     */
    HashMap<String, HashMap<Integer, LinkedList<Long>>> threadListByCategoryAndToxicStatus = new HashMap<>();


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




     /**
     * Add a user to the banned list.
     *
     * @param memberId     the member ID
     * @param banUntilDate the date when the ban is lifted
     * @param banReason    the reason for the ban
     */
    public void addBannedUserToCache(Long memberId, Date banUntilDate, String banReason) {
        bannedList.put(memberId, new BanInfo(banUntilDate, banReason));
    }

    /**
     * Check if a user is banned and whether their ban period has expired.
     *
     * @param userId the unique identifier of the user to check
     * @return true if the user is not banned or if the ban period has expired; false otherwise
     */
    public boolean checkBan(Long userId) {
        BanInfo banInfo = bannedList.get(userId);

        // If the user is not in the banned list, they are not banned.
        if (banInfo == null) {
            return true;
        }

        // Convert the ban expiration date to milliseconds.
        long banUntilMillis = banInfo.getBanUntilDate().getTime();

        // If the current time is greater than or equal to the ban expiry time, the ban has expired.
        if (System.currentTimeMillis() >= banUntilMillis) {
            // Optionally, you might want to remove the user from the banned list if the ban has expired.
            bannedList.remove(userId);
            return true;
        }

        // The user is banned and the ban period has not expired.
        return false;
    }

    /**
     * Get the banUntilDate by member ID.
     *
     * @param memberId the member ID
     * @return the banUntilDate or null if not banned
     */
    public Date getBanUntilDateByMemberId(Long memberId) {
        BanInfo banInfo = bannedList.get(memberId);
        return (banInfo != null) ? banInfo.getBanUntilDate() : null;
    }

    /**
     * Get the ban reason by member ID.
     *
     * @param memberId the member ID
     * @return the ban reason or null if not banned
     */
    public String getBanReasonByMemberId(Long memberId) {
        BanInfo banInfo = bannedList.get(memberId);
        return (banInfo != null) ? banInfo.getBanReason() : null;
    }

    /**
     * Removes a user from the banned list if their ban has expired.
     *
     * @param memberId the member ID
     */
    public void removeExpiredBan(Long memberId) {
        if (checkBan(memberId)) {
            bannedList.remove(memberId);
        }
    }
    /**
     * Adds a user to the system by initializing their associated data structures.
     * This method updates various internal data structures to keep track of the user's
     * information, such as their ID, and initializes lists to store their liked posts,
     * liked threads, created posts, and created threads.
     *
     * @param userId the unique identifier of the user to be added
     * @return true always, indicating that the user was successfully added to the system
     */
    public boolean addUser(long userId) {
        allMemberID.put(userId, userId);
        postLikeListByUser.put(userId, new HashSet<>());
        threadLikeListByUser.put(userId, new HashSet<>());
        postListByUser.put(userId, new HashSet<>());
        threadListByUser.put(userId, new HashSet<>());
        return true;
    }

    /**
     * Adds a thread to the cache with the specified parameters.
     * This method inserts a new thread into the cache. It initializes
     * parameters such as like count, view count, post count, creation date, and resolve status for the thread,
     * and categorizes it by content category, status, and user.
     *
     * @param threadId      the unique identifier of the thread
     * @param category      the category under which the thread is categorized
     * @param toxicStatus   the status of the thread (e.g., 1 for non-toxic, 0 for pending)
     * @param authorId        the unique identifier of the user who created the thread
     * @param resolveStatus the status of whether the thread is resolved (true for resolved, false for not resolved)
     * @param reason        the reason
     * @return true always, indicating that the thread was successfully added to the cache
     */
    public boolean addThreadToCache(long threadId, String category, ToxicStatusEnum toxicStatus, long authorId, boolean resolveStatus, String reason) {
        // Add thread ID to the allThreadID map
        allThreadID.put(threadId, threadId);

        // Initialize the thread parameters map, now allowing Object (for both Number and String)
        ConcurrentHashMap<String, Object> threadParaMap = new ConcurrentHashMap<>();
        threadParaMap.put("ThreadID", threadId);
        threadParaMap.put("LikeCount", 0);
        threadParaMap.put("ViewCount", 0);
        threadParaMap.put("PostCount", 0);
        threadParaMap.put("CreationDate", System.currentTimeMillis());
        threadParaMap.put("ResolveStatus", resolveStatus ? 1 : 0); // Set resolveStatus based on the passed parameter
        threadParaMap.put("Reason", reason); // Store the reason

        // Convert toxicStatus to a boolean number (1 for non-toxic, 0 for pending, -1 for toxic)
        int toxicStatusBooleanNumber;
        if (toxicStatus.equals("NOT-TOXIC")) {
            toxicStatusBooleanNumber = 1;
        } else if (toxicStatus.equals("TOXIC")) {
            toxicStatusBooleanNumber = -1;
        } else {
            toxicStatusBooleanNumber = 0;
        }
        threadParaMap.put("Status", toxicStatusBooleanNumber);

        // Add thread parameters to the cache
        parametersForAllThreads.put(threadId, threadParaMap);

        // Manage thread list by category and status
        threadListByCategoryAndToxicStatus.computeIfAbsent(category, k -> {
            HashMap<Integer, LinkedList<Long>> threadsByStatus = new HashMap<>();
            threadsByStatus.put(0, new LinkedList<>()); // Pending threads
            threadsByStatus.put(1, new LinkedList<>()); // Non-toxic threads
            threadsByStatus.put(-1, new LinkedList<>()); // Toxic threads
            return threadsByStatus;
        });

        threadListByCategoryAndToxicStatus.get(category).get(toxicStatusBooleanNumber).add(threadId);
        threadListByUser.get(authorId).add(threadId);

        // Initialize post list for this thread by toxic status
        LinkedList<Long> nonToxicPosts = new LinkedList<>();
        LinkedList<Long> pendingPosts = new LinkedList<>();
        HashMap<Integer, LinkedList<Long>> postListByStatus = new HashMap<>();
        postListByStatus.put(1, nonToxicPosts);
        postListByStatus.put(0, pendingPosts);
        postListByThreadIdAndToxicStatus.put(threadId, postListByStatus);

        return true;
    }


    /**
     * Increments the view count for a specified thread.
     * This method updates the view count for the thread identified by the given threadId,
     * increasing it by one each time the method is called.
     *
     * @param threadId the unique identifier of the thread to be viewed
     * @return true always, indicating that the view operation was successfully completed
     */
    public boolean threadViewIncrement(long threadId) {
        ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
        threadParaMap.put("ViewCount", (Integer) threadParaMap.get("ViewCount") + 1);
        return true;
    }




    /**
     * Changes the status of a specified thread within a given category and updates the appropriate data structures.
     * This method moves the thread identified by the given threadId from one status category to another.
     * If the status changes from 0 (pending) to 1 (non-toxic), the thread is added to the reportedThreads list.
     *
     * @param threadId       the unique identifier of the thread whose status is to be changed
     * @param category       the category under which the thread is categorized
     * @param newToxicStatus the new toxic status
     * @param reason         the reason for the status change (used if resolving the thread)
     * @return true if the status change was successfully performed; false if the status change is invalid
     */

    //resolveStatus = true  (modify parametersForAllThreads)
    //toxicStatus = newToxicStatusBooleanValue (convert from newToxicStatus) (modify threadListByCategoryAndToxicStatus)
    public boolean changeThreadToxicStatusFromModDashBoard(long threadId, String category, ToxicStatusEnum newToxicStatus, String reason) {
        // Convert the new toxic status to its corresponding boolean value using the helper method
        int newToxicStatusBooleanValue;
        try {
            newToxicStatusBooleanValue = convertStringToxicStatusToBooleanValue(newToxicStatus);
        } catch (RuntimeException e) {
            return false;  // Invalid status provided
        }

        // Retrieve the thread ID and thread parameters map
        Long threadID = allThreadID.get(threadId);
        if (threadID == null) {
            return false;  // Invalid threadId provided
        }

        ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
        if (threadParaMap == null) {
            return false;  // No parameters found for the given threadId
        }

        // Retrieve the current toxic status
        int oldToxicStatus = (Integer) threadParaMap.get("Status");

        // Update the toxic status and resolve status in the thread parameters map
        threadParaMap.put("Status", newToxicStatusBooleanValue);
        threadParaMap.put("Reason", reason);
        threadParaMap.put("ResolveStatus", 1); // Mark the thread as resolved

        // Remove the thread from the old category and status list if it exists
        LinkedList<Long> oldStatusList = threadListByCategoryAndToxicStatus.get(category).get(oldToxicStatus);
        if (oldStatusList != null) {
            oldStatusList.remove(threadId);
        }

        // Add the thread to the new category and status list
        threadListByCategoryAndToxicStatus.computeIfAbsent(category, k -> {
            HashMap<Integer, LinkedList<Long>> threadsByStatus = new HashMap<>();
            threadsByStatus.put(1, new LinkedList<>()); // Non-toxic threads
            threadsByStatus.put(0, new LinkedList<>()); // Pending threads
            threadsByStatus.put(-1, new LinkedList<>()); // Toxic threads
            return threadsByStatus;
        });
        threadListByCategoryAndToxicStatus.get(category).get(newToxicStatusBooleanValue).add(threadId);

        return true;
    }

    //resolveStatus = true  (modify parametersForAllThreads)
    //toxicStatus = newToxicStatusBooleanValue (convert from newToxicStatus) (modify threadListByCategoryAndToxicStatus)
    public boolean changeThreadToxicStatusForModBanningWhileSurfingForum(long threadId, String category, ToxicStatusEnum newToxicStatus, String reason) {
        // Convert the new toxic status to its corresponding boolean value using the helper method
        int newToxicStatusBooleanValue;
        try {
            newToxicStatusBooleanValue = convertStringToxicStatusToBooleanValue(newToxicStatus);
        } catch (RuntimeException e) {
            return false;  // Invalid status provided
        }

        // Retrieve the thread ID and thread parameters map
        Long threadID = allThreadID.get(threadId);
        if (threadID == null) {
            return false;  // Invalid threadId provided
        }

        ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
        if (threadParaMap == null) {
            return false;  // No parameters found for the given threadId
        }

        // Retrieve the current toxic status
        int oldToxicStatus = (Integer) threadParaMap.get("Status");

        // Update the toxic status and resolve status in the thread parameters map
        threadParaMap.put("Status", newToxicStatusBooleanValue);
        threadParaMap.put("Reason", reason);
        threadParaMap.put("ResolveStatus", 1); // Mark the thread as resolved

        // Remove the thread from the old category and status list if it exists
        LinkedList<Long> oldStatusList = threadListByCategoryAndToxicStatus.get(category).get(oldToxicStatus);
        if (oldStatusList != null) {
            oldStatusList.remove(threadId);
        }

        // Add the thread to the new category and status list
        threadListByCategoryAndToxicStatus.computeIfAbsent(category, k -> {
            HashMap<Integer, LinkedList<Long>> threadsByStatus = new HashMap<>();
            threadsByStatus.put(1, new LinkedList<>()); // Non-toxic threads
            threadsByStatus.put(0, new LinkedList<>()); // Pending threads
            threadsByStatus.put(-1, new LinkedList<>()); // Toxic threads
            return threadsByStatus;
        });
        threadListByCategoryAndToxicStatus.get(category).get(newToxicStatusBooleanValue).add(threadId);
        return true;
    }

    //method to change Thread's Resolved Status in the cache when owner try to update it (setResolveStatus = 0 - false)
    //this method update the parametersForAllThreads
    public boolean updateThreadResolveStatus(long threadId, boolean setResolveStatus) {
        // Retrieve the thread parameters map from the cache
        ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
        if (threadParaMap == null) {
            return false;  // No parameters found for the given threadId
        }

        // Update the resolveStatus (1 for true, 0 for false)
        threadParaMap.put("ResolveStatus", setResolveStatus ? 1 : 0);

        // Optionally: Log or perform additional actions here if needed

        return true;  // Indicate the status update was successful
    }

    //method to change Post's Resolved Status in the cache when owner try to update it (setResolveStatus = 0 - false)
    //this method update the parametersForAllPosts
    public boolean updatePostResolveStatus(long postId, boolean setResolveStatus) {
        // Retrieve the post parameters map from the cache
        ConcurrentHashMap<String, Object> postParaMap = parametersForAllPosts.get(postId);
        if (postParaMap == null) {
            return false;  // No parameters found for the given postId
        }

        // Update the resolveStatus (1 for true, 0 for false)
        postParaMap.put("ResolveStatus", setResolveStatus ? 1 : 0);

        // Optionally: Log or perform additional actions here if needed

        return true;  // Indicate the status update was successful
    }







    /**
     * Change thread toxic status for reporting and complaining to be pending (0) and return boolean if the process is sucess .
     *
     * @param threadId the thread id
     * @param category the category
     * @param reason   the reason
     * @return the boolean
     */
    // resolveStatus = true (modify parametersForAllThreads)
// newToxicStatusBooleanValue = 0 (pending) (modify threadListByCategoryAndToxicStatus)
    public boolean changeThreadToxicStatusForMemberReporting(long threadId, String category, String reason) {
        // Convert toxic status to PENDING (which corresponds to 0)
        int newToxicStatusBooleanValue = convertStringToxicStatusToBooleanValue(ToxicStatusEnum.PENDING);
        Long threadID = allThreadID.get(threadId);

        if (threadID == null) {
            return false;  // Invalid threadId provided
        }

        // Ensure the category exists in the threadListByCategoryAndToxicStatus map
        threadListByCategoryAndToxicStatus.computeIfAbsent(category, k -> {
            HashMap<Integer, LinkedList<Long>> threadsByStatus = new HashMap<>();
            threadsByStatus.put(0, new LinkedList<>()); // Pending threads
            threadsByStatus.put(1, new LinkedList<>()); // Non-toxic threads
            threadsByStatus.put(-1, new LinkedList<>()); // Toxic threads
            return threadsByStatus;
        });

        HashMap<Integer, LinkedList<Long>> statusMap = threadListByCategoryAndToxicStatus.get(category);
        LinkedList<Long> pendingList = statusMap.get(newToxicStatusBooleanValue);
        LinkedList<Long> nonToxicList = statusMap.get(1);

        // Check if the thread is in the "non-toxic" list
        if (nonToxicList.contains(threadID)) {
            // If it is in the "non-toxic" list, remove it from there
            nonToxicList.remove(threadID);
        }

        // Add the thread to the "pending" list (status 0)
        if (!pendingList.contains(threadID)) {
            pendingList.add(threadID);
        }

        // Update the thread parameters map to set resolveStatus to true
        ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
        if (threadParaMap != null) {
            threadParaMap.put("ResolveStatus", 1); // Mark the thread as resolved
            threadParaMap.put("Reason", reason);   // Optionally log the reason for the status change
        }

        return true;  // Indicating the status change was successful
    }




    // resolveStatus = true (modify parametersForAllPosts)
// toxicStatus = newToxicStatusBooleanValue (convert from newToxicStatus) (modify postListByThreadIdAndToxicStatus)
    public boolean changePostToxicStatusFromModDashboard(long postId, long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        // Convert the new toxic status to its corresponding integer value
        int newToxicStatusBooleanValue = convertStringToxicStatusToBooleanValue(newToxicStatus);

        // Retrieve the post ID
        Long postID = allPostId.get(postId);
        if (postID == null) {
            return false;  // Invalid postId provided
        }

        // Retrieve the post parameters map from the cache
        ConcurrentHashMap<String, Object> postParaMap = parametersForAllPosts.get(postId);
        if (postParaMap == null) {
            return false;  // No parameters found for the given postId
        }

        // Update the resolveStatus to true
        postParaMap.put("ResolveStatus", 1);  // Mark the post as resolved
        postParaMap.put("Reason", reason);  // Store the reason for the status change

        // Determine the current status to update the post list by thread ID and toxic status
        int currentToxicStatus = (Integer) postParaMap.get("Status");

        // If the current status is different from the new status, update the lists
        if (currentToxicStatus != newToxicStatusBooleanValue) {
            // Remove the post from the current toxic status list
            postListByThreadIdAndToxicStatus.get(threadId).get(currentToxicStatus).remove(postID);

            // Add the post to the new toxic status list
            postListByThreadIdAndToxicStatus.get(threadId).get(newToxicStatusBooleanValue).add(postID);

            // Update the toxic status in the post parameters map
            postParaMap.put("Status", newToxicStatusBooleanValue);
        }

        return true;  // Indicate the status change was successful
    }



    // resolveStatus = true (modify parametersForAllPosts)
// toxicStatus = newToxicStatusBooleanValue (convert from newToxicStatus) (modify postListByThreadIdAndToxicStatus)
    public boolean changePostToxicStatusForModBanningWhileSurfingForum(long postId, long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        // Convert the new toxic status to its corresponding integer value
        int newToxicStatusBooleanValue = convertStringToxicStatusToBooleanValue(newToxicStatus);

        // Retrieve the post ID
        Long postID = allPostId.get(postId);
        if (postID == null) {
            return false;  // Invalid postId provided
        }

        // Retrieve the post parameters map from the cache
        ConcurrentHashMap<String, Object> postParaMap = parametersForAllPosts.get(postId);
        if (postParaMap == null) {
            return false;  // No parameters found for the given postId
        }

        // Update the resolveStatus to true
        postParaMap.put("ResolveStatus", 1);  // Mark the post as resolved
        postParaMap.put("Reason", reason);  // Store the reason for the status change

        // Determine the current status to update the post list by thread ID and toxic status
        int currentToxicStatus = (Integer) postParaMap.get("Status");

        // If the current status is different from the new status, update the lists
        if (currentToxicStatus != newToxicStatusBooleanValue) {
            // Remove the post from the current toxic status list
            postListByThreadIdAndToxicStatus.get(threadId).get(currentToxicStatus).remove(postID);

            // Add the post to the new toxic status list
            postListByThreadIdAndToxicStatus.get(threadId).get(newToxicStatusBooleanValue).add(postID);

            // Update the toxic status in the post parameters map
            postParaMap.put("Status", newToxicStatusBooleanValue);
        }

        return true;  // Indicate the status change was successful
    }




    // resolveStatus = false (modify parametersForAllPosts)
// newToxicStatusBooleanValue = 0 (pending) (modify postListByThreadIdAndToxicStatus)
    public boolean changePostToxicStatusForMemberReporting(long postId, long threadId, String reason) {
        // Retrieve the post ID
        Long postID = allPostId.get(postId);
        if (postID == null) {
            return false;  // Invalid postId provided
        }

        // Retrieve the post parameters map from the cache
        ConcurrentHashMap<String, Object> postParaMap = parametersForAllPosts.get(postId);
        if (postParaMap == null) {
            return false;  // No parameters found for the given postId
        }

        // Set resolveStatus to false
        postParaMap.put("ResolveStatus", 0);  // Mark the post as unresolved
        postParaMap.put("Reason", reason);  // Store the reason for the status change

        // Convert the target toxic status to 0 (pending)
        int newToxicStatusBooleanValue = 0;  // Pending status

        // Determine the current toxic status
        int currentToxicStatus = (Integer) postParaMap.get("Status");

        // Handle the transition between toxic statuses
        if (currentToxicStatus != newToxicStatusBooleanValue) {
            // Remove the post from the current toxic status list
            postListByThreadIdAndToxicStatus.get(threadId).get(currentToxicStatus).remove(postID);

            // Add the post to the new toxic status list (pending)
            postListByThreadIdAndToxicStatus.get(threadId).get(newToxicStatusBooleanValue).add(postID);

            // Update the toxic status in the post parameters map
            postParaMap.put("Status", newToxicStatusBooleanValue);
        }

        return true;  // Indicate the status change was successful
    }


    /**
     * Adds a post to the cache by initializing like count and creation date
     * for the new post and categorizes it by status within the thread specified by the given thread id.
     * The new post is then added to the list of posts by the user specified by user id.
     *
     * @param threadId      the unique identifier of the thread to which the post belongs
     * @param postId        the unique identifier of the post to be added
     * @param authorId       the unique identifier of the user who created the post
     * @param toxicStatus   the status of the post (1 for non-toxic, 0 for pending)
     * @param resolveStatus the resolve status
     * @param reason        the reason of the post current toxicStatus state (null if NOT-TOXIC)
     * @return true if the post was successfully added to the cache; false if the status is invalid
     */
    //this method add post to both  postListByThreadIdAndToxicStatus and parametersForAllPosts

    public boolean addPostToCache(long threadId, long postId, long authorId, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        // Store post ID in the allPostId map
        allPostId.put(postId, postId);

        // Convert toxicStatus to a boolean number (1 for non-toxic, 0 for pending, -1 for toxic)
        int toxicStatusBooleanNumber;
        if (toxicStatus.equals("NOT-TOXIC")) {
            toxicStatusBooleanNumber = 1;
        } else if (toxicStatus.equals("TOXIC")) {
            toxicStatusBooleanNumber = -1;
        } else {
            toxicStatusBooleanNumber = 0;
        }

        // Ensure the thread's post list map is initialized
        postListByThreadIdAndToxicStatus.computeIfAbsent(threadId, k -> {
            HashMap<Integer, LinkedList<Long>> postsByStatus = new HashMap<>();
            postsByStatus.put(1, new LinkedList<>()); // Non-toxic posts
            postsByStatus.put(0, new LinkedList<>()); // Pending posts
            postsByStatus.put(-1, new LinkedList<>()); // Toxic posts
            return postsByStatus;
        });

        // Add the post to the appropriate list based on its toxic status
        postListByThreadIdAndToxicStatus.get(threadId).get(toxicStatusBooleanNumber).add(postId);

        // Initialize the post parameters
        ConcurrentHashMap<String, Object> postParaMap = new ConcurrentHashMap<>();
        postParaMap.put("LikeCount", 0);
        Long currentTime = System.currentTimeMillis();
        postParaMap.put("CreationDate", currentTime);
        postParaMap.put("Status", toxicStatusBooleanNumber);
        postParaMap.put("ResolveStatus", resolveStatus ? 1 : 0); // Set resolveStatus based on the passed parameter
        // Add the reason as a String
        postParaMap.put("Reason", reason);

        // Update the thread's latest post creation date if the post is non-toxic
        if (toxicStatusBooleanNumber == 1) {
            ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
            threadParaMap.put("PostCreationDate", currentTime);
        }
        // Store the post parameters in the cache
        parametersForAllPosts.put(postId, postParaMap);
        // Add the post to the user's list of posts
        postListByUser.get(authorId).add(postId);
        return true;
    }



    /**
     * Increments the like count for a specified post and records the user who liked it.
     * This method updates the like count for the post identified by the given postId,
     * and also adds the postId to the list of posts liked by the specified user.
     *
     * @param postId   the unique identifier of the post to be liked
     * @param userId   the unique identifier of the user who likes the post
     * @param threadId the thread id
     * @param mode     the mode
     * @return true always, indicating that the like operation was successfully completed
     */
    public boolean likePost(long postId, long userId, long threadId, int mode) {
        ConcurrentHashMap<String, Object> postParaMap = parametersForAllPosts.get(postId);
        ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
        if (mode == 1) {
            postParaMap.put("LikeCount", (Integer) postParaMap.get("LikeCount") + 1);
            threadParaMap.put("LikeCount", (Integer) threadParaMap.get("LikeCount") + 1);
        } else if (mode == 0) {
            postParaMap.put("LikeCount", (Integer) postParaMap.get("LikeCount") - 1);
            threadParaMap.put("LikeCount", (Integer) threadParaMap.get("LikeCount") - 1);
        } else {
            return false;
        }
        postLikeListByUser.get(userId).add(postId);
        return true;
    }

    /**
     * Retrieves the suggested threads for all users (a mechanism to calculate score based on creationDate, viewCounts, LikeCounts, PostCount, PostCreationDate).
     * This method iterates through all threads, calculates their relevancy scores,
     * and collects the details of threads with a status of 1 (non-toxic). The collected
     * threads are returned in a sorted order based on their relevancy scores.
     *
     * @return a HashMap containing 2 TreeMap, which contains thread details and user-specific information. The keys are "By Algorithm" and "By PostCreation"
     */
    public HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> getSuggestedThreads() {
        HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> returnCollection = new HashMap<>();
        Iterator<Long> iterator = parametersForAllThreads.keySet().iterator();
        TreeMap<BigDecimal, HashMap<String, Number>> returnCollectionByAlgorithm = new TreeMap<>();
        TreeMap<BigDecimal, HashMap<String, Number>> returnCollectionByPostCreation = new TreeMap<>();
        returnCollection.put("By Algorithm", returnCollectionByAlgorithm);
        returnCollection.put("By PostCreation", returnCollectionByPostCreation);

        while (iterator.hasNext()) {
            Long currentKey = iterator.next();
            ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(currentKey);
            if (threadParaMap.get("Status").equals(1)) {
                // This block calculates thread's score based on an algorithm and places them in the returnCollectionByAlgorithm TreeMap
                BigDecimal score = BigDecimal.valueOf(getThreadRelevancy(threadParaMap));
                score = ensureUniqueScore(returnCollectionByAlgorithm, score);
                HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, currentKey);
                returnCollectionByAlgorithm.put(score, returnedMap);

                // This block places threads inside the returnCollectionByPostCreation TreeMap with post creation dates being the keys
                BigDecimal postCreationDate = BigDecimal.valueOf(((Long) threadParaMap.get("PostCreationDate")).longValue());
                postCreationDate = ensureUniqueScore(returnCollectionByPostCreation, postCreationDate);
                returnCollectionByPostCreation.put(postCreationDate, returnedMap);
            }
        }
        return returnCollection;
    }

    private static BigDecimal ensureUniqueScore(TreeMap<BigDecimal, HashMap<String, Number>> collection, BigDecimal score) {
        if (collection.containsKey(score)) {
            score = score.add(BigDecimal.valueOf(0.000000001)); // Adding a small value to the score
            return ensureUniqueScore(collection, score);
        } else {
            return score;
        }
    }

    /**
     * Retrieves and publishes a limited number of non-toxic threads from a specified category.
     * This method retrieves threads from a specified category that have a status of non-toxic (status 1).
     * It starts from a given offset and retrieves up to the specified limit of threads. The retrieved
     * thread details are published using the provided SubmissionPublisher.
     *
     * @param category  the category from which threads are to be retrieved
     * @param limit     the maximum number of threads to retrieve
     * @param offset    the starting point for retrieval within the list of threads
     * @param publisher the SubmissionPublisher used to publish the retrieved thread details
     * @return true always, indicating that the retrieval and publishing operation was successfully completed
     */
    public boolean returnThreadByCategory(String category, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher) {
        LinkedList<Long> nonToxicThreadsList = threadListByCategoryAndToxicStatus.get(category).get(1);
        Iterator<Long> iterator = nonToxicThreadsList.listIterator(offset);
        int count = 1;
        while (iterator.hasNext() && count <= limit) {
            Long threadId = iterator.next();
            ConcurrentHashMap<String, Object> threadParaMap = parametersForAllThreads.get(threadId);
            HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, threadId);
            publisher.submit(returnedMap);
            count++;
        }
        return true;
    }

    /**
     * Retrieves and publishes a limited number of posts from a specified thread.
     * This method retrieves posts from a specified thread that have a status of non-toxic (status 1).
     * It starts from a given offset and retrieves up to the specified limit of posts. The retrieved
     * post details are published using the provided SubmissionPublisher.
     *
     * @param threadId  the unique identifier of the thread from which posts are to be retrieved
     * @param limit     the maximum number of posts to retrieve
     * @param offset    the starting point for retrieval within the list of posts
     * @param publisher the SubmissionPublisher used to publish the retrieved post details
     * @param userId    the unique identifier of the user (can be null)
     * @return true always, indicating that the retrieval and publishing operation was successfully completed
     */
    public boolean returnPostByThreadId(long threadId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher, Long userId) {
        LinkedList<Long> postList = postListByThreadIdAndToxicStatus.get(threadId).get(0);
        Iterator<Long> iterator = postList.listIterator(offset);
        int count = 1;
        while (iterator.hasNext() && count <= limit) {
            Long postId = iterator.next();
            ConcurrentHashMap<String, Object > postParaMap = parametersForAllPosts.get(postId);
            HashMap<String, Number> returnedPostMap = returnPostMapBuilder(postParaMap, userId, postId);
            publisher.submit(returnedPostMap);
            count++;
        }
        return true;
    }


    /**
     * Builds and returns a map containing post details and user-specific information.
     * This method creates a new map with details about a post, such as its ID, like count,
     * creation date, like status for a specific user, and report status.
     *
     * @param postParaMap a map containing various parameters of the post
     * @param userId      the unique identifier of the user (can be null)
     * @param postId      the unique identifier of the post
     * @return a map containing post details and user-specific information
     */
    private HashMap<String, Number> returnPostMapBuilder(ConcurrentHashMap<String, Object> postParaMap, Long userId, long postId) {
        HashMap<String, Number> returnedPostMap = new HashMap<>();
        returnedPostMap.put("LikeCount", (Integer) postParaMap.get("LikeCount"));
        returnedPostMap.put("PostID", postId);
        returnedPostMap.put("CreationDate", (Long) postParaMap.get("CreationDate"));

        if (userId == null) {
            returnedPostMap.put("LikeStatus", 0);
        } else {
            if (postLikeListByUser.get(userId).contains(postId)) {
                returnedPostMap.put("LikeStatus", 1);
            } else {
                returnedPostMap.put("LikeStatus", 0);
            }
        }

        return returnedPostMap;
    }

    /**
     * Builds and returns a map containing thread details and user-specific information.
     * This method creates a new map with details about a thread, such as its ID, like count, post count,
     * view count, creation date, like status for a specific user, and report status.
     *
     * @param cachedMap a map containing various cached parameters of the thread
     * @param threadId  the unique identifier of the thread
     * @return a map containing thread details and user-specific information
     */
    private HashMap<String, Number> returnThreadMapBuilder(ConcurrentHashMap<String, Object> cachedMap, long threadId) {
        HashMap<String, Number> returnedMap = new HashMap<>();
        returnedMap.put("ThreadID", threadId);
        returnedMap.put("LikeCount", (Integer) cachedMap.get("LikeCount"));
        returnedMap.put("PostCount", (Integer) cachedMap.get("PostCount"));
        returnedMap.put("ViewCount", (Integer) cachedMap.get("ViewCount"));
        returnedMap.put("CreationDate", (Long) cachedMap.get("CreationDate"));

        return returnedMap;
    }

    /**
     * Calculates the relevancy score of a thread based on its creation date, like count, post count, and view count.
     * This method computes the relevancy score for a thread using a combination of its age, like count, post count,
     * and view count. The relevancy score is determined by the formula:
     * (distance from today) * (like count + view count + post count), where the distance from today is calculated
     * as the ratio of the thread's creation date to the current time.
     *
     * @param threadParaMap a map containing various parameters of the thread, including its creation date, like count,
     *                      post count, and view count
     * @return the calculated relevancy score of the thread
     */
    private double getThreadRelevancy(ConcurrentHashMap<String, Object> threadParaMap) {
        // Cast the Object to Long and then call longValue()
        long primitiveThreadCreationDate = ((Long) threadParaMap.get("CreationDate")).longValue();

        double distanceFromToday = (double) primitiveThreadCreationDate / System.currentTimeMillis();

        // Cast the Object to Integer and then call intValue()
        int likeNum = ((Integer) threadParaMap.get("LikeCount")).intValue();
        int postNum = ((Integer) threadParaMap.get("PostCount")).intValue();
        int viewNum = ((Integer) threadParaMap.get("ViewCount")).intValue();

        return distanceFromToday * (likeNum + viewNum + postNum);
    }


    // Additional methods for thread and post mappers...

    /**
     * Gets post count of a thread by thread id.
     *
     * @param threadID the thread id
     * @return the post count of a thread by thread id
     */
    public Integer getPostCountOfAThreadByThreadId(Long threadID) {
        return getPostListByThreadIdAndToxicStatus().size();
    }

    /**
     * Check if a thread has been liked by a member id boolean.
     *
     * @param threadId the thread id
     * @param memberId the member id
     * @return the boolean
     */
    public boolean checkIfAThreadHasBeenLikedByAMemberId(Long threadId, Long memberId) {
        HashMap<Long, Set<Long>> threadLikeListByUser = getThreadLikeListByUser();

        if (threadLikeListByUser.containsKey(memberId)) {
            Set<Long> likedThreads = threadLikeListByUser.get(memberId);
            return likedThreads.contains(threadId);
        }
        return false;
    }


    /**
     * Gets thread view count by thread id.
     *
     * @param threadId the thread id
     * @return the thread view count by thread id
     */
    public Integer getThreadViewCountByThreadId(Long threadId) {
        ConcurrentHashMap<String,Object> threadParameters = parametersForAllThreads.get(threadId);
        if (threadParameters != null) {
            return (Integer) threadParameters.get("ViewCount");
        } else {
            return null;
        }
    }

    /**
     * Gets like count by thread id.
     *
     * @param threadId the thread id
     * @return the like count by thread id
     */
    public Integer getLikeCountByThreadId(Long threadId) {
        ConcurrentHashMap<String, Object> threadParameters = parametersForAllThreads.get(threadId);
        if (threadParameters != null) {
            return (Integer) threadParameters.get("LikeCount");
        } else {
            return null;
        }
    }

    /**
     * Gets post like count by post id.
     *
     * @param postId the post id
     * @return the post like count by post id
     */
    public Integer getPostLikeCountByPostId(Long postId) {
        ConcurrentHashMap<String, Object> postParameters = parametersForAllPosts.get(postId);
        if (postParameters != null) {
            return (Integer) postParameters.get("LikeCount");
        } else {
            return null;
        }
    }

    /**
     * Gets post view count by post id.
     *
     * @param postId the post id
     * @return the post view count by post id
     */
    public Integer getPostViewCountByPostId(Long postId) {
        ConcurrentHashMap<String, Object> postParameters = parametersForAllPosts.get(postId);
        if (postParameters != null) {
            return (Integer) postParameters.get("ViewCount");
        } else {
            return null;
        }
    }



    /**
     * Check if a post has been liked by a member id boolean.
     *
     * @param postId   the post id
     * @param memberId the member id
     * @return the boolean
     */
    public boolean checkIfAPostHasBeenLikedByAMemberId(Long postId, Long memberId) {
        Set<Long> likedPosts = postLikeListByUser.get(memberId);
        return likedPosts != null && likedPosts.contains(postId);
    }

    /**
     * Gets member total like count by member id.
     *
     * @param memberId the member id
     * @return the member total like count by member id
     */
    public Integer getMemberTotalLikeCountByMemberId(Long memberId) {
        int totalLikes = 0;

        Set<Long> threadIds = threadListByUser.get(memberId);
        if (threadIds != null) {
            for (Long threadId : threadIds) {
                Integer likeCount = getLikeCountByThreadId(threadId);
                if (likeCount != null) {
                    totalLikes += likeCount;
                }
            }
        }
        return totalLikes;
    }

    /**
     * Gets member total post count by member id.
     *
     * @param memberId the member id
     * @return the member total post count by member id
     */
    public Integer getMemberTotalPostCountByMemberId(Long memberId) {
        int totalPostCount = 0;

        Set<Long> threadIds = threadListByUser.get(memberId);
        if (threadIds != null) {
            for (Long threadId : threadIds) {
                Integer postCount = getPostCountOfAThreadByThreadId(threadId);
                if (postCount != null) {
                    totalPostCount += postCount;
                }
            }
        }

        Set<Long> postIds = postListByUser.get(memberId);
        if (postIds != null) {
            totalPostCount += postIds.size();
        }

        return totalPostCount;
    }




    //delete queue if LinedList of -1 is larger than 20 (check size every time adding element to that linked list
}
