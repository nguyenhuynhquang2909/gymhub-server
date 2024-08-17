package com.gymhub.gymhub.repository;

import com.gymhub.gymhub.actions.*;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import com.gymhub.gymhub.in_memory.BanInfo;
import com.gymhub.gymhub.in_memory.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.SubmissionPublisher;

@Repository
public class InMemoryRepository {

    @Autowired
    Cache cache;

    private static final String LOG_FILE_PATH = "src/main/resources/logs/cache-actions.log";
    private static long actionIdCounter = 0;

    public void logAction(MustLogAction action) {
        try (FileOutputStream fos = new FileOutputStream(LOG_FILE_PATH, true);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add User
    public boolean addUserToCache(long userId) {
        boolean result = cache.addUser(userId);
        AddUserAction action = new AddUserAction(++actionIdCounter, userId);
        logAction(action);
        return result;
    }

    // Add Thread
    public boolean addThreadToCache(long threadId, String category, ToxicStatusEnum toxicStatus, long authorId, boolean resolveStatus, String reason) {
        // Call the cache to add the thread
        boolean result = cache.addThreadToCache(threadId, category, toxicStatus, authorId, resolveStatus, reason);

        // Create a new AddThreadAction and log it
        AddThreadAction action = new AddThreadAction(++actionIdCounter, "AddThread", threadId, category, toxicStatus, authorId, resolveStatus, reason);
        logAction(action);

        return result;
    }

    // Change Thread Status for Reporting
    public boolean changeThreadStatusForMemberReporting(long threadId, String category, String reason) {
        // Invoke the method to change the thread toxic status
        boolean result = cache.changeThreadToxicStatusForMemberReporting(threadId, category, reason);

        // Convert the new toxic status to the corresponding enum
        ToxicStatusEnum toxicStatusEnum = ToxicStatusEnum.PENDING; // Since we're setting it to pending (0)

        // Create the ChangeThreadStatusAction object
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(
                ++actionIdCounter,
                "ChangeThreadStatusForReporting",
                threadId,
                category,
                toxicStatusEnum,
                true,  // resolveStatus is set to true based on your earlier logic
                reason
        );

        // Log the action
        logAction(action);

        return result;
    }

    // Change Thread Status from Mod Dashboard
    public boolean changeThreadToxicStatusFromModDashBoard(long threadId, String category, ToxicStatusEnum newStatus, String reason) {
        // Convert the new toxic status to its corresponding boolean value
        boolean result = cache.changeThreadToxicStatusFromModDashBoard(threadId, category, newStatus, reason);



        // Create a new ChangeThreadStatusAction object
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(
                ++actionIdCounter,
                "ChangeThreadStatusFromModDashboard", // actionType
                threadId,
                category,
                newStatus,
                true,  // resolveStatus is set to true based on the provided method logic
                reason
        );

        // Log the action
        logAction(action);

        return result;
    }

    public boolean changeThreadToxicStatusForModBanningWhileSurfingForum(long threadId, String category, ToxicStatusEnum newToxicStatus, String reason) {
        // Convert the new toxic status to its corresponding boolean value
        boolean result = cache.changeThreadToxicStatusForModBanningWhileSurfingForum(threadId, category, newToxicStatus, reason);


        // Create a new ChangeThreadStatusAction object
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(
                ++actionIdCounter,
                "ChangeThreadStatusForModBanningWhileSurfing", // actionType updated to reflect the specific context
                threadId,
                category,
                newToxicStatus,
                true,  // resolveStatus is set to true based on the provided method logic
                reason
        );

        // Log the action
        logAction(action);

        return result;
    }


    // Change Post Status

    public boolean changePostToxicStatusFromModDashboard(long postId, long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        // Call the cache method to change the post toxic status
        boolean result = cache.changePostToxicStatusFromModDashboard(postId, threadId, newToxicStatus, reason);

        // Create a new ChangePostStatusAction object and log the action
        ChangePostStatusAction action = new ChangePostStatusAction(
                ++actionIdCounter,  // Action ID counter incremented
                "changePostToxicStatusFromModDashboard",  // Action type
                postId,
                threadId,
                newToxicStatus,
                true,  // resolveStatus is true as per your previous logic
                reason
        );
        logAction(action);

        return result;
    }


    public boolean changePostToxicStatusForModBanningWhileSurfingForum(long postId, long threadId, ToxicStatusEnum newToxicStatus, String reason) {
        // Call the cache method to change the post toxic status
        boolean result = cache.changePostToxicStatusForModBanningWhileSurfingForum(postId, threadId,newToxicStatus, reason);



        // Create a new ChangePostStatusAction object and log the action
        ChangePostStatusAction action = new ChangePostStatusAction(
                ++actionIdCounter,  // Action ID counter incremented
                "changePostToxicStatusForModBanningWhileSurfingForum",  // Action type
                postId,
                threadId,
                newToxicStatus,
                true,  // Resolve status is true as per your logic
                reason
        );
        logAction(action);

        return result;
    }


    public boolean changePostToxicStatusForMemberReporting(long postId, long threadId, String reason) {

        // Call the cache method to change the post toxic status
        boolean result = cache.changePostToxicStatusForMemberReporting(postId, threadId, reason);

        // Create a new ChangePostStatusAction object and log the action
        ChangePostStatusAction action = new ChangePostStatusAction(
                ++actionIdCounter,  // Action ID counter incremented
                "changePostToxicStatusForMemberReporting",  // Action type
                postId,
                threadId,
                ToxicStatusEnum.PENDING,
                false,  // resolveStatus is set to false as per your logic
                reason
        );
        logAction(action);

        return result;
    }



    // Add Post to cache
    public boolean addPostToCache(long postId, long threadId, long userId, ToxicStatusEnum toxicStatus, boolean resolveStatus, String reason) {
        // Call the cache method to add the post
        boolean result = cache.addPostToCache(threadId, postId, userId, toxicStatus, resolveStatus, "");

        // Create a new AddPostAction object and log the action
        AddPostAction action = new AddPostAction(++actionIdCounter, threadId, postId, userId, toxicStatus, resolveStatus, "");
        logAction(action);

        return result;
    }


    // Like Post
    public boolean likePost(long postId, long userId, long threadId, int mode) {
        boolean result = cache.likePost(postId, userId, threadId, mode);
        LikePostAction action = new LikePostAction(++actionIdCounter, postId, userId, threadId, mode);
        logAction(action);
        return result;
    }

    // Suggested Threads
    public HashMap<String, TreeMap<BigDecimal, HashMap<String, Number>>> getSuggestedThreads() {
        return cache.getSuggestedThreads();
    }

    // Return Thread by Category
    public boolean returnThreadByCategory(String category, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher) {
        return cache.returnThreadByCategory(category, limit, offset, publisher);
    }

    // Return Post by Thread ID
    public boolean returnPostByThreadId(long threadId, int limit, int offset, SubmissionPublisher<HashMap<String, Number>> publisher, Long userId) {
        return cache.returnPostByThreadId(threadId, limit, offset, publisher, userId);
    }

    // Check Ban
    public boolean checkBan(Long userId) {
        return cache.checkBan(userId);
    }
    public void restoreFromLog() {
        try (FileInputStream fis = new FileInputStream(LOG_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    MustLogAction action = (MustLogAction) ois.readObject();

                    if (action instanceof AddUserAction) {
                        cache.addUser(((AddUserAction) action).getUserId());
                    } else if (action instanceof AddThreadAction) {
                        AddThreadAction addThreadAction = (AddThreadAction) action;
                        cache.addThreadToCache(
                                addThreadAction.getThreadId(),
                                addThreadAction.getCategory(),
                                addThreadAction.getToxicStatus(),
                                addThreadAction.getAuthorId(),
                                addThreadAction.isResolveStatus(),
                                addThreadAction.getReason()
                        );
                    } else if (action instanceof ChangeThreadStatusAction) {
                        ChangeThreadStatusAction changeThreadStatusAction = (ChangeThreadStatusAction) action;
                        cache.changeThreadToxicStatusFromModDashBoard(
                                changeThreadStatusAction.getThreadId(),
                                changeThreadStatusAction.getCategory(),
                                changeThreadStatusAction.getToxicStatus(),
                                changeThreadStatusAction.getReason()
                        );
                    } else if (action instanceof ChangePostStatusAction) {
                        ChangePostStatusAction changePostStatusAction = (ChangePostStatusAction) action;
                        cache.changePostToxicStatusFromModDashboard(
                                changePostStatusAction.getPostId(),
                                changePostStatusAction.getThreadId(),
                                changePostStatusAction.getToxicStatus(),
                                changePostStatusAction.getReason()
                        );
                    } else if (action instanceof AddPostAction) {
                        AddPostAction addPostAction = (AddPostAction) action;
                        cache.addPostToCache(
                                addPostAction.getThreadId(),
                                addPostAction.getPostId(),
                                addPostAction.getUserId(),
                                addPostAction.getToxicStatus(),
                                addPostAction.isResolveStatus(),
                                addPostAction.getReason()

                        );
                    } else if (action instanceof LikePostAction) {
                        LikePostAction likePostAction = (LikePostAction) action;
                        cache.likePost(
                                likePostAction.getPostId(),
                                likePostAction.getUserId(),
                                likePostAction.getThreadId(),
                                likePostAction.getMode()
                        );
                    }
                } catch (Exception e) {
                    break;  // End of file reached or other exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Get Thread List by Category and Status
    public HashMap<Integer, LinkedList<Long>> getThreadListByCategoryAndStatus(String category) {
        return cache.getThreadListByCategoryAndToxicStatus().getOrDefault(category, new HashMap<>());
    }

    // Get Pending Threads
    public HashMap<String, HashMap<Integer, LinkedList<Long>>> getPendingThreads() {
        HashMap<String, HashMap<Integer, LinkedList<Long>>> pendingThreadsByCategory = new HashMap<>();

        for (Map.Entry<String, HashMap<Integer, LinkedList<Long>>> categoryEntry : cache.getThreadListByCategoryAndToxicStatus().entrySet()) {
            String category = categoryEntry.getKey();
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

    /**
     * Saves a banned user with the ban information (banUntilMillis and reason).
     *
     * @param userId  The ID of the user to be banned.
     * @param banInfo The BanInfo object containing ban details.
     */
    public void saveBannedUser(Long userId, BanInfo banInfo) {
        cache.getBannedList().put(userId, banInfo);
    }

    /**
     * Deletes a banned user from the banned list.
     *
     * @param userId The ID of the user to be unbanned.
     */
    public void deleteBannedUser(Long userId) {
        cache.getBannedList().remove(userId);
    }

    /**
     * Finds the ban expiration time for a user.
     *
     * @param userId The ID of the user.
     * @return The ban expiration time in milliseconds, or null if the user is not banned.
     */
    public Long findBanExpiration(Long userId) {
        BanInfo banInfo = cache.getBannedList().get(userId);
        return (banInfo != null) ? banInfo.getBanUntilDate().getTime() : null;
    }

    /**
     * Checks if a user is banned.
     *
     * @param userId The ID of the user.
     * @return True if the user is banned, false otherwise.
     */
    public boolean isUserBanned(Long userId) {
        return cache.getBannedList().containsKey(userId);
    }

    /**
     * Finds the reason for a user's ban.
     *
     * @param userId The ID of the user.
     * @return The reason for the ban, or null if the user is not banned.
     */
    public String findBanReason(Long userId) {
        BanInfo banInfo = cache.getBannedList().get(userId);
        return (banInfo != null) ? banInfo.getBanReason() : null;
    }
}
