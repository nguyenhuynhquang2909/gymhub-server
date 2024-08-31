package com.gymhub.gymhub;

import com.gymhub.gymhub.actions.AddThreadAction;
import com.gymhub.gymhub.actions.AddUserAction;
import com.gymhub.gymhub.actions.MustLogAction;
import com.gymhub.gymhub.domain.ForumAccount;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.PostRepository;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;

import static com.gymhub.gymhub.repository.InMemoryRepository.LOG_FILE_PATH;


@SpringBootApplication
@RestController
@EnableJpaRepositories(basePackages = "com.gymhub.gymhub.repository")
@EntityScan(basePackages = "com.gymhub.gymhub.domain")

public class GymhubApplication {

	@Autowired
	Cache cache;
	@Autowired
	InMemoryRepository inMemoryRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ThreadRepository threadRepository;
	@Autowired
	PostRepository postRepository;
    @Autowired
    private OrderedFormContentFilter formContentFilter;

	public static void main(String[] args) {
		SpringApplication.run(GymhubApplication.class, args);
	}

	private void readAction() {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(LOG_FILE_PATH))) {
			System.out.println("Starting read action");
			while (true) {
				try {
					MustLogAction obj = (MustLogAction) in.readObject();
					System.out.println("Deserialized: " + obj);
				} catch (EOFException eof) {
					// End of file reached
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	//later on: replace cacheFill with InMemoryRepositiory.restoreFromLog


	//TODO Write a post construct method that read from the log and fill in the cache by calling the corresponding methods

@PostConstruct
	private void restoreCache(){
		inMemoryRepository.restoreFromLog();
	System.out.println("Thread toxic Status " + cache.getThreadListByCategoryAndToxicStatus());
		System.out.println("Post toxic Status " + cache.getPostListByThreadIdAndToxicStatus());
		System.out.println("Posts in cache: " + cache.getParametersForAllPosts()); // Assuming getPosts() returns all posts in cache

}



//	@PostConstruct
	private void cacheFill(){
		System.out.println("Duong hello test ");
//		List<Thread> mockThreadList = threadRepository.findByCategory(ThreadCategoryEnum.ADVICE);
//		System.out.println("Mock thread list" + mockThreadList.size());

		List<Member> members = memberRepository.findAll();
//		System.out.println("List of members " + members);
		Iterator<Member> iterator = members.iterator();

		while(iterator.hasNext()){
//			System.out.println("Looping through list of members");
			inMemoryRepository.addUserToCache (iterator.next().getId());
		}

		List<Thread> threads = threadRepository.findAll();
		Iterator<Thread> iterator2 = threads.iterator();
		while(iterator2.hasNext()){
//			System.out.println("Looping through list of threads");
			Thread thread = iterator2.next();
			inMemoryRepository.addThreadToCache(thread.getId(), thread.getCategory(), thread.getCreationDateTime(),ToxicStatusEnum.NOT_TOXIC, thread.getOwner().getId(), false, "");
		}


		List<Post> posts = postRepository.findAll();
//		System.out.println("List of posts " + posts);
		Iterator<Post> iterator3 = posts.iterator();
		while(iterator3.hasNext()){
//			System.out.println("Looping through list of posts");
			Post post = iterator3.next();
			//System.out.println();
			inMemoryRepository.addPostToCache(post.getThread().getId(), post.getId(), post.getAuthor().getId(),  ToxicStatusEnum.NOT_TOXIC, false, "");
		}

		System.out.println("Cache Initialization: Done");
		System.out.println("Swagger UI is available at http://localhost:8080/swagger-ui/index.html");
// Print cache contents to verify
		System.out.println("Cache Contents:");
//		System.out.println("Para" + cache.getParametersForAllThreads());

//		System.out.println("All posts by  thread id and toxic status " + cache.getPostListByThreadIdAndToxicStatus());
		System.out.println("All threads by category and toxic status " );
		for (Map.Entry<ThreadCategoryEnum, HashMap<Integer, LinkedList<Long>>> categoryEntry : cache.getThreadListByCategoryAndToxicStatus().entrySet()) {
			ThreadCategoryEnum category = categoryEntry.getKey();
			HashMap<Integer, LinkedList<Long>> statusMap = categoryEntry.getValue();

			for (Map.Entry<Integer, LinkedList<Long>> statusEntry : statusMap.entrySet()) {
				Integer status = statusEntry.getKey();
				LinkedList<Long> threadIds = statusEntry.getValue();

				for (Long threadId : threadIds) {
					System.out.println("Category: " + category + ", Status: " + status + ", Thread ID: " + threadId);
				}
			}
		}




		// Assuming you have methods to retrieve cached data
		//System.out.println("Thread list with user ID "+ cache.getThreadListByUser());
		//Long userId = 1L; // Replace with the actual user ID you want to query
//		System.out.println("Threads in cache: " + cache.getParametersForAllThreads()); // Assuming getParametersForAllThreads() returns a map of threads
//		System.out.println("Thread toxic Status " + cache.getThreadListByCategoryAndToxicStatus());
//		System.out.println("Post toxic Status " + cache.getPostListByThreadIdAndToxicStatus());
//		System.out.println("Posts in cache: " + cache.getParametersForAllPosts()); // Assuming getPosts() returns all posts in cache



		//read log file (call method)
readAction();

	}

	

}
