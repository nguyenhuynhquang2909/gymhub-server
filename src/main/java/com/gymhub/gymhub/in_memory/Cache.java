package com.gymhub.gymhub.in_memory;

import io.swagger.v3.oas.models.links.Link;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.glassfish.jaxb.core.v2.TODO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

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
     * A map storing parameters for all threads, keyed by thread ID.
     * Each thread's parameter map contains:
     * - "LikeCount" (Integer): The number of likes the thread has received.
     * - "ViewCount" (Integer): The number of views the thread has received.
     * - "PostCount" (Integer): The number of posts in the thread.
     * - "CreationDate" (Long): The creation date of the thread (in milliseconds).
     * - "Status" (Integer): The status of the thread (e.g., 1 for non-toxic, 0 for pending).
     */
    LinkedHashMap<Long, ConcurrentHashMap<String, Number>> parametersForAllThreads = new LinkedHashMap<>();

    /**
     * A map storing parameters for all posts, keyed by post ID.
     * Each post's parameter map contains:
     * - "LikeCount" (Integer): The number of likes the post has received.
     * - "CreationDate" (Long): The creation date of the post (in milliseconds).
     */
    HashMap<Long, ConcurrentHashMap<String, Number>> parametersForAllPosts = new LinkedHashMap<>();

    /**
     * A map categorizing threads by category and status, with lists of thread IDs.
     */
    HashMap<String, HashMap<Integer, LinkedList<Long>>> threadListByCategoryAndStatus = new HashMap<>();

    /**
     * A map categorizing posts by thread ID and status, with lists of post IDs.
     */
    HashMap<Long, HashMap<Integer, LinkedList<Long>>> postListByThreadIdAndStatus = new HashMap<>();

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
     * A set of reported thread IDs.
     */
    Set<Long> reportedThreads = new HashSet<>();

    /**
     * A set of reported post IDs.
     */
    Set<Long> reportedPosts = new HashSet<>();

    /**
     * A map containing the ids of banned user and the date (in millisecond) their bans are lifted
     */
    HashMap<Long, Long> bannedList = new HashMap<>();



    /**
     * Adds a user to the system by initializing their associated data structures.
     * This method updates various internal data structures to keep track of the user's
     * information, such as their ID, and initializes lists to store their liked posts,
     * liked threads, created posts, and created threads.
     * @param userId the unique identifier of the user to be added
     * @return true always, indicating that the user was successfully added to the system
     */

    //TODO This action must be logged. Create a subclass extending Action class for this method
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
     * parameters such as like count, view count, post count, and creation date for the thread,
     * and categorizes it by content category, status and user.
     *
     * @param threadId the unique identifier of the thread
     * @param category the category under which the thread is categorized
     * @param status the status of the thread (e.g., 1: non-toxic, 0: pending)
     * @param userId the unique identifier of the user who created the thread
     * @return true always, indicating that the thread was successfully added to the cache
     */
    //TODO This action must be logged. Create a subclass extending Action class for this method
    public boolean addThreadToCache(long threadId, String category, int status, long userId){
        allThreadID.put(threadId, threadId);
        ConcurrentHashMap<String, Number> threadParaMap = new ConcurrentHashMap<>();
        threadParaMap.put("LikeCount", 0);
        threadParaMap.put("ViewCount", 0);
        threadParaMap.put("PostCount", 0);
        threadParaMap.put("CreationDate", System.currentTimeMillis());
        threadParaMap.put("Status", status);
        parametersForAllThreads.put(threadId, threadParaMap);
        if (!threadListByCategoryAndStatus.containsKey(category)){
            HashMap<Integer, LinkedList<Long>> threadsByStatus = new HashMap<>();
            threadsByStatus.put(0, new LinkedList<>());
            threadsByStatus.put(1, new LinkedList<>());
            threadListByCategoryAndStatus.put(category, threadsByStatus);
        }
        threadListByCategoryAndStatus.get(category).get(status).add(threadId);
        threadListByUser.get(userId).add(threadId);
        LinkedList<Long> nonToxicPosts = new LinkedList<>();
        LinkedList<Long> pendingPosts = new LinkedList<>();
        HashMap<Integer, LinkedList<Long>> postListByStatus = new HashMap<>();
        postListByStatus.put(1, nonToxicPosts);
        postListByStatus.put(0, pendingPosts);
        postListByThreadIdAndStatus.put(threadId, postListByStatus);

        return true;
    }

    /**
     * Adjusts the like count for a specified thread based on the given mode and records the user who performed the action.
     * This method updates the like count for the thread identified by the given threadId.
     * If the mode is 1, the like count is incremented; if the mode is 0, the like count is decremented.
     * The threadId is also added to the list of threads liked by the specified user.
     * @param threadId the unique identifier of the thread to be liked or unliked
     * @param userId the unique identifier of the user who likes or unlikes the thread
     * @param mode the mode of the operation (1 to like, 0 to unlike)
     * @return true always, indicating that the like or unlike operation was successfully completed
     */

  


    /**
     * Increments the view count for a specified thread.
     * This method updates the view count for the thread identified by the given threadId,
     * increasing it by one each time the method is called.
     * @param threadId the unique identifier of the thread to be viewed
     * @return true always, indicating that the view operation was successfully completed
     */
    //TODO This action must be logged. Create a subclass extending Action class for this method
    public boolean viewThread(long threadId){
        ConcurrentHashMap<String, Number> threadParaMap = parametersForAllThreads.get(threadId);
        threadParaMap.put("ViewCount", (Integer) threadParaMap.get("ViewCount") + 1);
        return true;
    }

    /**
     * Changes the status of a specified thread within a given category and updates the appropriate data structures.
     * This method moves the thread identified by the given threadId from one status category to another.
     * If the status changes from 0 (pending) to 1 (non-toxic, the thread is added to the reportedThreads list.
     * @param threadId the unique identifier of the thread whose status is to be changed
     * @param category the category under which the thread is categorized
     * @param from the current status of the thread (e.g., 1: non-toxic, 0: pending)
     * @param to the new status of the thread (e.g., 1: non-toxic, 0: pending)
     * @return true if the status change was successfully performed; false if the status change is invalid
     */
    //TODO This action must be logged. Create a subclass extending Action class for this method
    public boolean changeThreadStatus(long threadId, String category, int from, int to){
        Long threadID = allThreadID.get(threadId);
        if (from == 0 && to == 1){
            threadListByCategoryAndStatus.get(category).get(0).remove(threadID);
            threadListByCategoryAndStatus.get(category).get(1).add(threadID);
            reportedThreads.add(threadID);
            return true;
        }
        else if (from == 1 && to == 0){
            threadListByCategoryAndStatus.get(category).get(1).remove(threadID);
            threadListByCategoryAndStatus.get(category).get(0).add(threadID);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Changes the status of a specified post that belongs to the thread identified by a given threadId
     * This method moves the post identified by the postId from one status category to another.
     * If the status changes from 0 (pending) to 1 (non-toxic), the thread is added to the reportedPosts list.
     * @param postId the unique identifier of the post in concern
     * @param threadId the unique identifier of the thread the post belongs to
     * @param from the current status of the thread (e.g., 1: non-toxic, 0: pending)
     * @param to the new status of the thread (e.g., 1: non-toxic, 0: pending)
     * @return true if the status change was successfully performed; false if the status change is invalid
     */
    //TODO This action must be logged. Create a subclass extending Action class for this method
    public boolean changePostStatus(long postId, long threadId, String category, int from, int to){
        Long postID = allPostId.get(postId);
        if (from == 0 && to == 1){
            postListByThreadIdAndStatus.get(threadId).get(0).remove(postID);
            postListByThreadIdAndStatus.get(threadId).get(1).add(postID);
            reportedPosts.add(postID);
            return true;
        }
        else if (from == 1 && to == 0){
            postListByThreadIdAndStatus.get(threadId).get(1).remove(postID);
            postListByThreadIdAndStatus.get(threadId).get(0).add(postID);
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Adds a post to the cache by initializing like count and creation date
     * for the new post and categorizes it by status within the thread specified by the given thread id.
     * The new post í then added to the list of posts by the user specified by user id.
     * @param threadId the unique identifier of the thread to which the post belongs
     * @param postId the unique identifier of the post to be added
     * @param userId the unique identifier of the user who created the post
     * @param status the status of the post (1 for non-toxic, 0 for pending)
     * @return true if the post was successfully added to the cache; false if the status is invalid
     */
    //TODO This action must be logged. Create a subclass extending Action class for this method
    public boolean addPostToCache(long threadId, long postId, long userId, int status){
        allPostId.put(postId, postId);
        if (status == 1){
            postListByThreadIdAndStatus.get(threadId).get(0).add(postId);
        }
        else if (status == 0){
            postListByThreadIdAndStatus.get(threadId).get(1).add(postId);
        }
        else {
            return false;
        }
        ConcurrentHashMap<String, Number> postParaMap = new ConcurrentHashMap<>();
        postParaMap.put("LikeCount", 0);
        postParaMap.put("CreationDate", System.currentTimeMillis());
        parametersForAllPosts.put(postId, postParaMap);
        postListByUser.get(userId).add(postId);
        return true;
    }

    /**
     * Increments the like count for a specified post and records the user who liked it.
     * This method updates the like count for the post identified by the given postId,
     * and also adds the postId to the list of posts liked by the specified user.
     * @param postId the unique identifier of the post to be liked
     * @param userId the unique identifier of the user who likes the post
     * @return true always, indicating that the like operation was successfully completed
     */
    //TODO This action must be logged. Create a subclass extending Action class for this method
    public boolean likePost(long postId, long userId, int mode){
        ConcurrentHashMap<String, Number> postParaMap = parametersForAllPosts.get(postId);
        if (mode == 1){
            postParaMap.put("LikeCount", (Integer) postParaMap.get("LikeCount") + 1);
        }
        else if (mode == 0){
            postParaMap.put("LikeCount", (Integer) postParaMap.get("LikeCount") - 1);
        }
        else {
            return false;
        }
        postLikeListByUser.get(userId).add(postId);
        return true;
    }

    /**
     * Retrieves the suggested thread for all user (a mechanism to calculate score based on creationDate, viewCounts, LikeCounts, numberOfPosts)
     * This method iterates through all threads, calculates their relevancy scores,
     * and collects the details of threads with a status of 1 (non-toxic). The collected
     * threads are returned in a sorted order based on their relevancy scores.

     * @return a TreeMap where the keys are the relevancy scores and the values are maps containing
     *         thread details and user-specific information
     */

    public TreeMap<Double, HashMap<String, Number>> getSuggestedThreads() {

        Iterator<Long> iterator = parametersForAllThreads.keySet().iterator();
        TreeMap<Double, HashMap<String, Number>> returnCollection = new TreeMap<>();

        while (iterator.hasNext()) {
            Long currentKey = iterator.next();
            ConcurrentHashMap<String, Number> threadParaMap = parametersForAllThreads.get(currentKey);
            if (threadParaMap.get("Status").equals(1)) {
                double score = getThreadRelevancy(threadParaMap);
                score = ensureUniqueScore(returnCollection, score);
                HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, currentKey);
                returnCollection.put(score, returnedMap);
            }
        }
        return returnCollection;
    }
    public TreeMap<Double, HashMap<String, Number>> getLatestDicussionThreads() {

        return null;
    }

    private double ensureUniqueScore(TreeMap<Double, HashMap<String, Number>> collection, double score) {
        if (collection.containsKey(score)) {
            score += 0.000000001;
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
     * @param category the category from which threads are to be retrieved
     * @param userId the unique identifier of the user (can be null)
     * @param limit the maximum number of threads to retrieve
     * @param offset the starting point for retrieval within the list of threads
     * @param publisher the SubmissionPublisher used to publish the retrieved thread details
     * @return true always, indicating that the retrieval and publishing operation was successfully completed
     */
    public boolean returnThreadByCategory(String category, Long userId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher){
        LinkedList<Long> nonToxicThreadsList  = threadListByCategoryAndStatus.get(category).get(1);
        Iterator<Long> iterator = nonToxicThreadsList.listIterator(offset);
        int count = 1;
        while(iterator.hasNext() && count <= limit){
            Long threadId = iterator.next();
            ConcurrentHashMap<String, Number> threadParaMap = parametersForAllThreads.get(threadId);
            HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, threadId);
            publisher.submit(returnedMap);
            count++;

        }
        return true;
    }

    /**
     * Retrieves and publishes a limited number of posts from a specified thread.
     * This method retrieves posts from a specified thread that have a status of non-toxic (status 0).
     * It starts from a given offset and retrieves up to the specified limit of posts. The retrieved
     * post details are published using the provided SubmissionPublisher.
     * @param threadId the unique identifier of the thread from which posts are to be retrieved
     * @param limit the maximum number of posts to retrieve
     * @param offset the starting point for retrieval within the list of posts
     * @param publisher the SubmissionPublisher used to publish the retrieved post details
     * @param userId the unique identifier of the user (can be null)
     * @return true always, indicating that the retrieval and publishing operation was successfully completed
     */
    public boolean returnPostByThreadId(long threadId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher, Long userId){
        LinkedList<Long> postList = postListByThreadIdAndStatus.get(threadId).get(0);
        Iterator<Long> iterator = postList.listIterator(offset);
        int count = 1;
        while(iterator.hasNext() && count <= limit){
            Long postId = iterator.next();
            ConcurrentHashMap<String, Number> postParaMap = parametersForAllPosts.get(postId);
            HashMap<String, Number> returnedPostMap = returnPostMapBuilder(postParaMap, userId, postId);
            publisher.submit(returnedPostMap);
            count++;
        }
        return true;


    }

    /**
     * Checks if a user is banned and whether their ban period has expired.
     * This method verifies if the specified user is in the banned list. If the user is not banned,
     * it returns true. If the user is banned, it checks whether the current time is greater than
     * or equal to the ban expiry time. If the ban period has expired, it returns true; otherwise, it returns false.
     * @param userId the unique identifier of the user to check
     * @return true if the user is not banned or if the ban period has expired; false otherwise
     */
    public boolean checkBan(Long userId){
        if (!bannedList.containsKey(userId)){
            return true;
        }
        else {
            if (System.currentTimeMillis() >= bannedList.get(userId)){
                return true;
            }
            else {
                return false;
            }
        }
    }


    /**
     * Builds and returns a map containing post details and user-specific information.
     * This method creates a new map with details about a post, such as its ID, like count,
     * creation date, like status for a specific user, and report status. The like status
     * indicates whether the user has liked the post (0 - no, 1 - yes), and the report status indicates
     * whether the post has been reported (0 - no, 1 - yes).
     * @param postParaMap a map containing various parameters of the post
     * @param userId the unique identifier of the user (can be null)
     * @param postId the unique identifier of the post
     * @return a map containing post details and user-specific information
     */
    private HashMap<String, Number> returnPostMapBuilder(ConcurrentHashMap<String, Number> postParaMap, Long userId, long postId){
        HashMap<String, Number> returnedPostMap = new HashMap<>();
        returnedPostMap.put("LikeCount", postParaMap.get("LikeCount"));
        returnedPostMap.put("id", postId);
        returnedPostMap.put("CreationDate", postParaMap.get("CreationDate"));
        if (userId == null){
            returnedPostMap.put("LikeStatus", 0);
        }
        else {
            if (postLikeListByUser.get(userId).contains(postId)){
                returnedPostMap.put("LikeStatus", 1);
            }
            else {
                returnedPostMap.put("LikeStatus", 0);
            }
        }
        if (reportedPosts.contains(postId)){
            returnedPostMap.put("ReportStatus", 1);
        }
        else {
            returnedPostMap.put("ReportStatus", 0);
        }
        return returnedPostMap;
    }

    /**
     * Builds and returns a map containing thread details and user-specific information.
     * This method creates a new map with details about a thread, such as its ID, like count, post count,
     * view count, creation date, like status for a specific user, and report status. The like status
     * indicates whether the user has liked the thread (0 - no, 1 - yes), and the report status indicates whether the thread
     * has been reported (0 - no, 1 - yes).
     * @param cachedMap a map containing various cached parameters of the thread

     * @param threadId the unique identifier of the thread
     * @return a map containing thread details and user-specific information
     */
    private HashMap<String, Number> returnThreadMapBuilder(ConcurrentHashMap<String, Number> cachedMap , long threadId ){
        HashMap<String, Number> returnedMap = new HashMap<>();
        returnedMap.put("id", threadId);
        returnedMap.put("LikeCount", cachedMap.get("LikeCount"));
        returnedMap.put("PostCount", cachedMap.get("PostCount"));
        returnedMap.put("ViewCount", cachedMap.get("ViewCount"));
        returnedMap.put("CreationDate", cachedMap.get("CreationDate"));



        if (reportedThreads.contains(threadId)){
            returnedMap.put("Report Status", 1);
        }
        else {
            returnedMap.put("Report Status", 0);
        }
        return returnedMap;

    }

    /**
     * Calculates the relevancy score of a thread based on its creation date, like count, post count, and view count.
     * This method computes the relevancy score for a thread using a combination of its age, like count, post count,
     * and view count. The relevancy score is determined by the formula:
     * (distance from today) * (like count + view count + post count), where the distance from today is calculated
     * as the ratio of the thread's creation date to the current time.
     * @param threadParaMap a map containing various parameters of the thread, including its creation date, like count,
     * post count, and view count
     * @return the calculated relevancy score of the thread
     */
    private double getThreadRelevancy(ConcurrentHashMap<String, Number> threadParaMap){
        long primitiveThreadCreationDate = threadParaMap.get("CreationDate").longValue();
        double distanceFromToday = (double) primitiveThreadCreationDate / System.currentTimeMillis();
        int likeNum = threadParaMap.get("LikeCount").intValue();
        int postNum = threadParaMap.get("PostCount").intValue();
        int viewNum = threadParaMap.get("ViewCount").intValue();
        return (distanceFromToday) * (likeNum + viewNum + postNum);

    }



}
