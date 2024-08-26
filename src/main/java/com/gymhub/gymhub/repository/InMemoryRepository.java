package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.actions.*;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import com.gymhub.gymhub.helper.HelperMethod;
import com.gymhub.gymhub.in_memory.BanInfo;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.in_memory.CacheManipulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;

import static com.gymhub.gymhub.helper.HelperMethod.convertStringToxicStatusToBooleanValue;

/**
 * The type In memory repository.
 */
@Repository
public class InMemoryRepository {
    //
    @Autowired
    Cache cache;
    @Autowired
    CacheManipulation cacheManipulation;

    private static final String LOG_FILE_PATH = "src/main/resources/logs/cache-actions.log";

    private static long actionIdCounter = 0;
    @Autowired
    ThreadRepository threadRepository;


    @Autowired
    PostRepository postRepository;

    /**
     * Methods to log cache action to file for future cache restore
     */

    public void logAction(MustLogAction action) {
        try (FileOutputStream fos = new FileOutputStream(LOG_FILE_PATH, true);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Restore from log
    public void restoreFromLog() {
        try (FileInputStream fis = new FileInputStream(LOG_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    MustLogAction action = (MustLogAction) ois.readObject();
                    if (action instanceof AddUserAction) {
                        cache.getAllMemberID().put(((AddUserAction) action).getUserId(), ((AddUserAction) action).getUserId());
                    } else if (action instanceof AddThreadAction addThreadAction) {
                        addThreadToCache(
                                addThreadAction.getThreadId(),
                                addThreadAction.getCategory(),
                                addThreadAction.getToxicStatus(),
                                addThreadAction.getAuthorId(),
                                addThreadAction.isResolveStatus(),
                                addThreadAction.getReason()
                        );
                    } else if (action instanceof ChangeThreadStatusAction changeThreadStatusAction) {
                        changeThreadToxicStatusFromModDashBoard(
                                changeThreadStatusAction.getThreadId(),
                                changeThreadStatusAction.getCategory(),
                                changeThreadStatusAction.getToxicStatus(),
                                changeThreadStatusAction.getReason()
                        );
                    } else if (action instanceof ChangePostStatusAction changePostStatusAction) {
                        changePostToxicStatusFromModDashboard(
                                changePostStatusAction.getPostId(),
                                changePostStatusAction.getThreadId(),
                                changePostStatusAction.getToxicStatus(),
                                changePostStatusAction.getReason()
                        );
                    } else if (action instanceof AddPostAction addPostAction) {
                        addPostToCache(
                                addPostAction.getPostId(),
                                addPostAction.getThreadId(),
                                addPostAction.getUserId(),
                                addPostAction.getToxicStatus(),
                                addPostAction.isResolveStatus(),
                                addPostAction.getReason()
                        );
                    } else if (action instanceof LikePostAction likePostAction) {
                        likePost(
                                likePostAction.getPostId(),
                                likePostAction.getUserId(),
                                likePostAction.getThreadId(),
                                likePostAction.getMode()
                        );
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * METHODS TO ADD DOMAIN ENTITY TO CACHE
     */


    // Add User
    public boolean addUserToCache(long userId) {
        if (cacheManipulation.addUserToCache(userId)){
            AddUserAction action = new AddUserAction(userId);
            logAction(action);
            return true;
        }
        return false;
    }


    public boolean addPostToCache(long threadId, long postId, long userId, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        if (cacheManipulation.addPostToCache(postId, threadId, userId, toxicStatus, resolveStatus, reason)) {
            // Log the action
            AddPostAction action = new AddPostAction(threadId, postId, userId, toxicStatus, resolveStatus, reason);
            logAction(action);

            return true;
        }
        return false;

    }


    public boolean addThreadToCache(long threadId, ThreadCategoryEnum category, ToxicStatusEnum toxicStatus, long authorId, boolean resolveStatus, String reason) {
        if (cacheManipulation.addThreadToCache(threadId, category, toxicStatus, authorId, resolveStatus, reason)) {
            // Log the action
            AddThreadAction action = new AddThreadAction(threadId, category, toxicStatus, authorId, resolveStatus, reason);
            logAction(action);
            return true;
        }
        return false;
    }


    /**
     * Methods to GET domain entities from cache
     */


    // Suggested Threads
    public HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> getSuggestedThreads() {
        HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> returnCollection = new HashMap<>();
        TreeMap<BigDecimal, HashMap<String, Number>> returnCollectionByAlgorithm = new TreeMap<>();
        TreeMap<BigDecimal, HashMap<String, Number>> returnCollectionByPostCreation = new TreeMap<>();

        returnCollection.put("By Algorithm", returnCollectionByAlgorithm);
        returnCollection.put("By PostCreation", returnCollectionByPostCreation);

        for (Map.Entry<Long, ConcurrentHashMap<String, Object>> entry : cache.getParametersForAllThreads().entrySet()) {
            Long threadId = entry.getKey();
            ConcurrentHashMap<String, Object> threadParaMap = entry.getValue();

            if (threadParaMap.get("ToxicStatus").equals(1)) {
                BigDecimal score = BigDecimal.valueOf(getThreadRelevancy(threadParaMap));
                score = ensureUniqueScore(returnCollectionByAlgorithm, score);
                HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, threadId);
                returnCollectionByAlgorithm.put(score, returnedMap);

                BigDecimal postCreationDate = BigDecimal.valueOf(((Long) threadParaMap.get("PostCreationDate")).longValue());
                postCreationDate = ensureUniqueScore(returnCollectionByPostCreation, postCreationDate);
                returnCollectionByPostCreation.put(postCreationDate, returnedMap);
            }
        }

        return returnCollection;
    }

    /**
     * Returns all thread IDs for the specified category.
     *
     * @param category the category to filter threads by
     * @return a list of thread IDs in the specified category
     */
    public HashMap<Integer, LinkedList<Long>> getAllThreadIdsByCategory(ThreadCategoryEnum category) {
        // Debugging: Print the entire cache for inspection using the category object itself
        System.out.println("Cache content: " + cache.getThreadListByCategoryAndToxicStatus().getOrDefault(category, new HashMap<>()));

        // Use the category object directly as the key
        return cache.getThreadListByCategoryAndToxicStatus().getOrDefault(category, new HashMap<>());
    }


    public LinkedList<Long> getThreadIdsByCategoryAndStatus(ThreadCategoryEnum category, int status) {
        // Get the map associated with the category
        HashMap<Integer, LinkedList<Long>> statusMap = cache.getThreadListByCategoryAndToxicStatus().get(category);

        if (statusMap != null) {
            // Return the list of thread IDs for the given status
            return statusMap.getOrDefault(status, new LinkedList<>());
        } else {
            // Return an empty list if the category is not found
            return new LinkedList<>();
        }
    }

    // Get Pending Threads
    public HashMap<ThreadCategoryEnum, HashMap<Integer, LinkedList<Long>>> getPendingThreads() {
        HashMap<ThreadCategoryEnum, HashMap<Integer, LinkedList<Long>>> pendingThreadsByCategory = new HashMap<>();

        for (Map.Entry<ThreadCategoryEnum, HashMap<Integer, LinkedList<Long>>> categoryEntry : cache.getThreadListByCategoryAndToxicStatus().entrySet()) {
            ThreadCategoryEnum category = categoryEntry.getKey();
            HashMap<Integer, LinkedList<Long>> threadsByStatus = categoryEntry.getValue();

            LinkedList<Long> pendingThreadsInCategory = threadsByStatus.get(0);

            if (pendingThreadsInCategory != null) {
                HashMap<Integer, LinkedList<Long>> pendingStatusMap = new HashMap<>();
                pendingStatusMap.put(0, new LinkedList<>(pendingThreadsInCategory));

                pendingThreadsByCategory.put(category, pendingStatusMap);
            }
        }

        return pendingThreadsByCategory;
    }


    // Get Pending Posts
    public HashMap<Long, HashMap<Integer, LinkedList<Long>>> getPendingPosts() {
        HashMap<Long, HashMap<Integer, LinkedList<Long>>> pendingPostsByThread = new HashMap<>();

        for (Map.Entry<Long, HashMap<Integer, LinkedList<Long>>> threadEntry : cache.getPostListByThreadIdAndToxicStatus().entrySet()) {
            Long threadId = threadEntry.getKey();
            HashMap<Integer, LinkedList<Long>> postsByStatus = threadEntry.getValue();

            LinkedList<Long> pendingPostsInThread = postsByStatus.get(0);

            if (pendingPostsInThread != null) {
                HashMap<Integer, LinkedList<Long>> pendingStatusMap = new HashMap<>();
                pendingStatusMap.put(0, new LinkedList<>(pendingPostsInThread));

                pendingPostsByThread.put(threadId, pendingStatusMap);
            }
        }

        return pendingPostsByThread;
    }


    // Return Thread by Category
    public boolean returnThreadByCategory(ThreadCategoryEnum category, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher) {
        LinkedList<Long> nonToxicThreadsList = cache.getThreadListByCategoryAndToxicStatus().get(category).get(1);
        Iterator<Long> iterator = nonToxicThreadsList.listIterator(offset);
        int count = 1;

        while (iterator.hasNext() && count <= limit) {
            Long threadId = iterator.next();
            ConcurrentHashMap<String, Object> threadParaMap = cache.getParametersForAllThreads().get(threadId);
            HashMap<String, Number> returnedMap = returnThreadMapBuilder(threadParaMap, threadId);
            publisher.submit(returnedMap);
            count++;
        }

        return true;
    }

    // Return Post by Thread ID
    public boolean returnPostByThreadId(long threadId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher, Long userId) {
        LinkedList<Long> postList = cache.getPostListByThreadIdAndToxicStatus().get(threadId).get(0);
        Iterator<Long> iterator = postList.listIterator(offset);
        int count = 1;

        while (iterator.hasNext() && count <= limit) {
            Long postId = iterator.next();
            ConcurrentHashMap<String, Object> postParaMap = cache.getParametersForAllPosts().get(postId);
            HashMap<String, Number> returnedPostMap = returnPostMapBuilder(postParaMap, userId, postId);
            publisher.submit(returnedPostMap);
            count++;
        }

        return true;
    }

    /**
     * Methods to change toxic status of threads and posts
     */
    // Change Thread Status for Reporting
    public boolean changeThreadToxicStatusForMemberReporting(long threadId, ThreadCategoryEnum category,  ToxicStatusEnum newStatus, String reason) {
        cacheManipulation.changeThreadToxicStatus(threadId, category, newStatus, reason);
        // Log the action
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(threadId, category, ToxicStatusEnum.PENDING, true, reason);
        logAction(action);

        return true;
    }

    // Change Thread Status from Mod Dashboard
    public boolean changeThreadToxicStatusFromModDashBoard(long threadId, ThreadCategoryEnum category, ToxicStatusEnum newStatus, String reason) {
        cacheManipulation.changeThreadToxicStatus(threadId, category, newStatus, reason);

        // Log the action
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(threadId, category, newStatus, true, reason);
        logAction(action);

        return true;
    }

    public boolean changeThreadToxicStatusForModBanningWhileSurfingForum(long threadId, ThreadCategoryEnum category, ToxicStatusEnum newToxicStatus, String reason) {
        cacheManipulation.changeThreadToxicStatus(threadId, category, newToxicStatus, reason);
        // Log the action
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(threadId, category, newToxicStatus, true, reason);
        logAction(action);

        return true;
    }

    // Change Post Status
    public boolean changePostToxicStatusFromModDashboard(long postId, long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        cacheManipulation.changePostToxicStatus(postId, threadId, newToxicStatus, reason);
        // Log the action
        ChangePostStatusAction action = new ChangePostStatusAction(postId, threadId, newToxicStatus, true, reason);
        logAction(action);

        return true;
    }

    public boolean changePostToxicStatusForModBanningWhileSurfingForum(long postId, long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        cacheManipulation.changePostToxicStatus(postId, threadId, newToxicStatus, reason);
        // Log the action
        ChangePostStatusAction action = new ChangePostStatusAction(postId, threadId, newToxicStatus, true, reason);
        logAction(action);

        return true;
    }

    public boolean changePostToxicStatusForMemberReporting(long postId, long threadId,  ToxicStatusEnum newToxicStatus, String reason) {
        cacheManipulation.changePostToxicStatus(postId, threadId, newToxicStatus, reason);
        // Log the action
        ChangePostStatusAction action = new ChangePostStatusAction(postId, threadId, ToxicStatusEnum.PENDING, false, reason);
        logAction(action);

        return true;
    }

    /**
     * Methods for likes operation
     */

    // Like Post
    public boolean likePost(long postId, long userId, long threadId, int mode) {
        if (cacheManipulation.likePost(postId, userId, threadId, mode)) {
            // Log the action
            LikePostAction action = new LikePostAction(postId, userId, threadId, mode);
            logAction(action);
            return true;
        }
        return false;
    }


    /**
     * Methods for Banning Operations
     */
    public void saveBannedUser(Long userId, BanInfo banInfo) {
        cache.getBannedList().put(userId, banInfo);
    }

    public void deleteBannedUser(Long userId) {
        cache.getBannedList().remove(userId);
    }

    public Long findBanExpiration(Long userId) {
        BanInfo banInfo = cache.getBannedList().get(userId);
        return (banInfo != null) ? banInfo.getBanUntilDate().getTime() : null;
    }

    public boolean isUserBanned(Long userId) {
        return cache.getBannedList().containsKey(userId);
    }

    public String findBanReason(Long userId) {
        BanInfo banInfo = cache.getBannedList().get(userId);
        return (banInfo != null) ? banInfo.getBanReason() : null;
    }

    public boolean checkBanStatus(Long userID){
        return (!cache.getBannedList().containsKey(userID) ||  (cache.getBannedList().containsKey(userID) && findBanExpiration(userID) <= System.currentTimeMillis()));
    }


    /**
     * Methods to return map builders for post and threads
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

    private HashMap<String, Number> returnPostMapBuilder(ConcurrentHashMap<String, Object> postParaMap, Long userId, long postId) {
        HashMap<String, Number> returnedPostMap = new HashMap<>();
        returnedPostMap.put("LikeCount", (Integer) postParaMap.get("LikeCount"));
        returnedPostMap.put("PostID", postId);
        returnedPostMap.put("CreationDate", (Long) postParaMap.get("CreationDate"));

        if (userId == null) {
            returnedPostMap.put("LikeStatus", 0);
        } else {
            if (cache.getPostLikeListByUser().get(userId).contains(postId)) {
                returnedPostMap.put("LikeStatus", 1);
            } else {
                returnedPostMap.put("LikeStatus", 0);
            }
        }

        return returnedPostMap;
    }

    /**
     * Methods calculate and display thread's relevancy points
     */

    private double getThreadRelevancy(ConcurrentHashMap<String, Object> threadParaMap) {
        long primitiveThreadCreationDate = (Long) threadParaMap.get("CreationDate");
        double distanceFromToday = (double) primitiveThreadCreationDate / System.currentTimeMillis();

        int likeNum = (Integer) threadParaMap.get("LikeCount");
        int postNum = (Integer) threadParaMap.get("PostCount");
        int viewNum = (Integer) threadParaMap.get("ViewCount");

        return distanceFromToday * (likeNum + viewNum + postNum);
    }


    private static BigDecimal ensureUniqueScore(TreeMap<BigDecimal, HashMap<String, Number>> collection, BigDecimal score) {
        if (collection.containsKey(score)) {
            score = score.add(BigDecimal.valueOf(0.000000001));
            return ensureUniqueScore(collection, score);
        } else {
            return score;
        }
    }

    /**
     Methods to return posts/thread statistic (views, likes, postCounts, latestPost)
     */

    /**
     * Gets post count of a thread by thread id.
     *
     * @param threadID the thread id
     * @return the post count of a thread by thread id
     */
    public Integer getPostCountOfAThreadByThreadId(Long threadID) {
        return cache.getPostListByThreadIdAndToxicStatus().get(threadID).size();
    }

    /**
     * Check if a thread has been liked by a member id boolean.
     *
     * @param threadId the thread id
     * @param memberId the member id
     * @return the boolean
     */
    public boolean checkIfAThreadHasBeenLikedByAMemberId(Long threadId, Long memberId) {
        HashMap<Long, Set<Long>> threadLikeListByUser = cache.getThreadLikeListByUser();

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
        ConcurrentHashMap<String, Object> threadParameters = cache.getParametersForAllThreads().get(threadId);
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
        ConcurrentHashMap<String, Object> threadParameters = cache.getParametersForAllPosts().get(threadId);
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
        ConcurrentHashMap<String, Object> postParameters = cache.getParametersForAllPosts().get(postId);
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
        ConcurrentHashMap<String, Object> postParameters = cache.getParametersForAllPosts().get(postId);
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
        Set<Long> likedPosts = cache.getPostLikeListByUser().get(memberId);
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

        Set<Long> threadIds = cache.getThreadListByUser().get(memberId);
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

        Set<Long> threadIds = cache.getThreadListByUser().get(memberId);
        if (threadIds != null) {
            for (Long threadId : threadIds) {
                Integer postCount = getPostCountOfAThreadByThreadId(threadId);
                if (postCount != null) {
                    totalPostCount += postCount;
                }
            }
        }

        Set<Long> postIds = cache.getPostLikeListByUser().get(memberId);
        if (postIds != null) {
            totalPostCount += postIds.size();
        }

        return totalPostCount;
    }


    public String getReasonByThreadId(Long threadId) {
        // Check if the thread exists in the map
        if (cache.getParametersForAllThreads().containsKey(threadId)) {
            // Retrieve the parameter map for the thread
            ConcurrentHashMap<String, Object> threadParameters = cache.getParametersForAllThreads().get(threadId);
            // Return the toxicStatus if it exists, otherwise return null
            return (String) threadParameters.getOrDefault("Reason", "");
        } else {
            // If the thread ID is not found, return null or throw an exception based on your requirements
            return null;
        }
    }

    // Method to get the resolveStatus of a post by its ID
    public boolean getResolveStatusByPostId(Long postId) {
        // Check if the post exists in the map
        if (cache.getParametersForAllPosts().containsKey(postId)) {
            // Retrieve the parameter map for the post
            ConcurrentHashMap<String, Object> postParameters = cache.getParametersForAllPosts().get(postId);
            // Return the resolveStatus if it exists, otherwise return null
            return (boolean) postParameters.getOrDefault("ResolveStatus", false);
        } else {
            throw new RuntimeException("postId " + postId + " not found");
        }
    }

    // Method to get the toxicStatus of a post by its ID
    public ToxicStatusEnum getToxicStatusByPostId(Long postId) {
        // Check if the post exists in the map
        if (cache.getParametersForAllPosts().containsKey(postId)) {
            // Retrieve the parameter map for the post
            ConcurrentHashMap<String, Object> postParameters = cache.getParametersForAllPosts().get(postId);
            // Return the toxicStatus if it exists, otherwise return null
            return (ToxicStatusEnum) postParameters.getOrDefault("toxicStatus", null);
        } else {
            // If the post ID is not found, return null or throw an exception based on your requirements
            return null;
        }
    }


    public String getReasonByPostId(Long postId) {
        // Check if the post exists in the map
        if (cache.getParametersForAllPosts().containsKey(postId)) {
            // Retrieve the parameter map for the post
            ConcurrentHashMap<String, Object> postParameters = cache.getParametersForAllPosts().get(postId);
            // Return the resolveStatus if it exists, otherwise return null
            return (String) postParameters.getOrDefault("Reason", "");
        } else {
            throw new RuntimeException("postId " + postId + " not found");
        }
    }


    public Set<Long> getFollowersId(Long memberId) {
        return cache.getFollowersCache().getOrDefault(memberId, ConcurrentHashMap.newKeySet());
    }

    public Set<Long> getFollowingId(Long memberId) {
        return cache.getFollowingCache().getOrDefault(memberId, ConcurrentHashMap.newKeySet());
    }
    /**
     * Follow another member.
     *
     * @param followerId The ID of the member who wants to follow.
     * @param followingId The ID of the member to be followed.
     */
    public void follow(Long followerId, Long followingId) {
        cache.getFollowingCache().computeIfAbsent(followerId, k -> ConcurrentHashMap.newKeySet()).add(followingId);
        cache.getFollowersCache().computeIfAbsent(followingId, k -> ConcurrentHashMap.newKeySet()).add(followerId);
    }


    /**
     * Unfollow another member.
     *
     * @param followerId The ID of the member who wants to unfollow.
     * @param followingId The ID of the member to be unfollowed.
     */
    public void unfollow(Long followerId, Long followingId) {
        Set<Long> follwingSet = cache.getFollowingCache().get(followerId);
        if (follwingSet != null) {
            follwingSet.remove(followerId);
        }
        Set<Long> followersSet = cache.getFollowersCache().get(followingId);
        if (followersSet != null) {
            followersSet.remove(followerId);
        }
    }


    public Date getBanUntilDateByMemberId(Long memberId) {
        BanInfo banInfo = cache.getBannedList().get(memberId);
        return (banInfo != null) ? banInfo.getBanUntilDate() : null;
    }


    /**
     * Get the number of followers for a member.
     *
     * @param memberId The ID of the member.
     * @return The number of followers.
     */
    public int getFollowersNumber(Long memberId) {
        Set<Long> followers = cache.getFollowersCache().get(memberId);
        return followers != null ? followers.size() : 0;
    }

    /**
     * Get the number of members a user is following.
     *
     * @param memberId The ID of the member.
     * @return The number of members the user is following.
     */
    public int getFollowingNumber(Long memberId) {
        Set<Long> following = cache.getFollowingCache().get(memberId);
        return following != null ? following.size() : 0;
    }

    // Method to get the resolveStatus of a thread by its ID
    public boolean getResolveStatusByThreadId(Long threadId) {
        // Check if the thread exists in the map
        if (cache.getParametersForAllThreads().containsKey(threadId)) {
            // Retrieve the parameter map for the thread
            ConcurrentHashMap<String, Object> threadParameters = cache.getParametersForAllThreads().get(threadId);
            // Return the resolveStatus if it exists, otherwise return null
            int resolveStatusInt =  (int) threadParameters.getOrDefault("ResolveStatus", false);
          if (resolveStatusInt == 1){
              return  true;
          }else {
              return false;
          }

        } else {
            // If the thread ID is not found, return null or throw an exception based on your requirements
            throw new RuntimeException("Thread not found");
        }
    }


    public Integer getToxicStatusByThreadId(Long threadId) {
        // Check if the thread exists in the map
        if (cache.getParametersForAllThreads().containsKey(threadId)) {
            // Retrieve the parameter map for the thread
            ConcurrentHashMap<String, Object> threadParameters = cache.getParametersForAllThreads().get(threadId);
            // Return the toxicStatus if it exists, otherwise return null
            return (Integer) threadParameters.getOrDefault("ToxicStatus", 1);
        } else {
            // If the thread ID is not found, return null or throw an exception based on your requirements
            return null;
        }
    }


    /**
     * METHODS TO AUTOMATICALLY DELETE TOXIC THREADS AND POST WHEN REACHING A SIZE LIMIT
     */

    private void deleteToxicThreadsAndClearList(ThreadCategoryEnum category) {
        LinkedList<Long> toxicList = cache.getThreadListByCategoryAndToxicStatus().get(category).get(-1);

        if (toxicList != null && !toxicList.isEmpty()) {
            // Find all threads with IDs in the toxic list and delete them from the database
            List<Thread> threadsToDelete = threadRepository.findAllById(toxicList);
            threadRepository.deleteAll(threadsToDelete);

            // Clear the toxic list after deletion
            toxicList.clear();
        }
    }


    private void deleteToxicPostsAndClearList(long threadId) {
        // Get the toxic post list for the thread
        HashMap<Integer, LinkedList<Long>> statusMap = cache.getPostListByThreadIdAndToxicStatus().get(threadId);
        LinkedList<Long> toxicPostList = statusMap.get(-1);

        if (toxicPostList != null && !toxicPostList.isEmpty()) {
            // Find and delete all toxic posts from the database
            List<Post> postsToDelete = postRepository.findAllById(toxicPostList);
            postRepository.deleteAll(postsToDelete);
            // Clear the toxic post list
            toxicPostList.clear();
        }

    }
}
