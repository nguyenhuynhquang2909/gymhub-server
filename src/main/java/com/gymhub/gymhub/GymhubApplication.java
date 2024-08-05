package com.gymhub.gymhub;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.in_memory.Cache;
import com.gymhub.gymhub.repository.PostRepository;
import com.gymhub.gymhub.repository.ThreadRepository;
import com.gymhub.gymhub.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@SpringBootApplication
@RestController
@EnableJpaRepositories(basePackages = "com.gymhub.gymhub.repository")
@EntityScan(basePackages = "com.gymhub.gymhub.domain")

public class GymhubApplication {
	@Autowired
	Cache cache;
	@Autowired
	UserRepository userRepository;
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
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		java.lang.Thread javaThread = new java.lang.Thread(new Runnable() {
			@Override
			public void run() {
				List<Member> members = userRepository.findAll();
				fillMembersToCache(members);

				Future<List<Thread>> threadFuture = getAllThreadsFromDb(executorService);
				Future<List<Post>> postFuture = getAllPostFromDb(executorService);
				boolean threadCacheFilled = false;
				while (true){
					if (threadFuture.isDone()){
						if (!threadCacheFilled){
							Random random = new Random();
                            List<Thread> threads;
                            try {
                                threads = threadFuture.get();
								System.out.println("Start Processing");
								fillThreadsToCache(threads, random);
								threadCacheFilled = true;
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }


						}
						try {
							if (postFuture.isDone()){
								List<Post> posts = postFuture.get();
								System.out.println("Start Processing");
								fillPostsToCache(posts);
								break;
							}

						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						} catch (ExecutionException e) {
							throw new RuntimeException(e);
						}


                    }
				}

			}

		});

	}

	private Future<List<Thread>> getAllThreadsFromDb(ExecutorService executorService){
		return executorService.submit(()->{
			System.out.println("Start fetching threads");
			return threadRepository.findAll();
		});

	}

	private Future<List<Post>> getAllPostFromDb(ExecutorService executorService){
		return executorService.submit(()->{
			System.out.println("Start fetching posts");
			return postRepository.findAll();
		});

	}

	private void fillMembersToCache(List<Member> members){
		Iterator<Member> iterator = members.iterator();
		while(iterator.hasNext()){
			cache.addUser(iterator.next().getId());
		}
		System.out.println("All Members Loaded");
	}

	private void fillThreadsToCache(List<Thread> threads, Random random){
		Iterator<Thread> iterator2 = threads.iterator();
		while(iterator2.hasNext()){
			Thread thread = iterator2.next();
			int rand = random.nextInt(1, 4);
			cache.addThreadToCache(thread.getId(), "flexing", 1, thread.getAuthor().getId());

			if(rand == 1){
				cache.addThreadToCache(thread.getId(), "flexing", 1, thread.getAuthor().getId());
			}
			else if (rand == 2){
				cache.addThreadToCache(thread.getId(), "advises", 1, thread.getAuthor().getId());
			}
			else {
				cache.addThreadToCache(thread.getId(), "supplement", 1, thread.getAuthor().getId());
			}
		}
		System.out.println("ALL Threads Loaded");
	}

	private void fillPostsToCache(List<Post> posts){
		Iterator<Post> iterator3 = posts.iterator();
		while(iterator3.hasNext()){
			Post post = iterator3.next();
			cache.addPostToCache(post.getThread().getId(), post.getId(), post.getAuthor().getId(), 1);
		}
		System.out.println("ALL Posts Loaded");
	}

}
