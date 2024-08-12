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
		System.out.println("Duong hello test ");
		List<Member> members = userRepository.findAll();
		Iterator<Member> iterator = members.iterator();

		while(iterator.hasNext()){
			cache.addUser(iterator.next().getId());
		}

		List<Thread> threads = threadRepository.findAll();
		Iterator<Thread> iterator2 = threads.iterator();
		while(iterator2.hasNext()){
			Thread thread = iterator2.next();
				cache.addThreadToCache(thread.getId(), thread.getCategory(), "notToxic", thread.getOwner().getId());
		}

		List<Post> posts = postRepository.findAll();
		Iterator<Post> iterator3 = posts.iterator();
		while(iterator3.hasNext()){
			Post post = iterator3.next();
			cache.addPostToCache(post.getThread().getId(), post.getId(), post.getAuthor().getId(), 1);
		}

		System.out.println("Cache Initialization: Done");
		System.out.println("Swagger UI is available at ");




	}

	

}
