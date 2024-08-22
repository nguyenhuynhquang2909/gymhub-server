package com.gymhub.gymhub;

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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;


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
	
	public static void main(String[] args) {
		SpringApplication.run(GymhubApplication.class, args);
	}

	//TODO Write a post construct method that read from the log and fill in the cache by calling the corresponding methods
	@PostConstruct
	private void cacheFill(){
		System.out.println("Duong hello test ");
//		List<Thread> mockThreadList = threadRepository.findByCategory(ThreadCategoryEnum.ADVICE);
//		System.out.println("Mock thread list" + mockThreadList.size());

		List<Member> members = memberRepository.findAll();
		System.out.println("List of members " + members);
		Iterator<Member> iterator = members.iterator();

		while(iterator.hasNext()){
//			System.out.println("Looping through list of members");
			inMemoryRepository.addUserToCache (iterator.next().getId());
		}

		List<Thread> threads = threadRepository.findAll();
		System.out.println("List of threads " + threads);
		Iterator<Thread> iterator2 = threads.iterator();
		while(iterator2.hasNext()){
//			System.out.println("Looping through list of threads");
			Thread thread = iterator2.next();
				inMemoryRepository.addThreadToCache(thread.getId(), thread.getCategory(), ToxicStatusEnum.NOT_TOXIC, thread.getOwner().getId(), false, "");
		}

		List<Post> posts = postRepository.findAll();
		System.out.println("List of posts " + posts);
		Iterator<Post> iterator3 = posts.iterator();
		while(iterator3.hasNext()){
//			System.out.println("Looping through list of posts");
			Post post = iterator3.next();
			System.out.println();
			inMemoryRepository.addPostToCache(post.getThread().getId(), post.getId(), post.getAuthor().getId(),  ToxicStatusEnum.NOT_TOXIC, false, "");
		}

		System.out.println("Cache Initialization: Done");
		System.out.println("Swagger UI is available at http://localhost:8080/swagger-ui/index.html");


// Print cache contents to verify
		System.out.println("Cache Contents:");

		// Assuming you have methods to retrieve cached data
		System.out.println("Threads in cache: " + cache.getParametersForAllThreads()); // Assuming getParametersForAllThreads() returns a map of threads
		System.out.println("Thread toxic Status " + cache.getThreadListByCategoryAndToxicStatus());
		System.out.println("Post toxic Status " + cache.getPostListByThreadIdAndToxicStatus());
		System.out.println("Posts in cache: " + cache.getParametersForAllPosts()); // Assuming getPosts() returns all posts in cache

	}

	

}
