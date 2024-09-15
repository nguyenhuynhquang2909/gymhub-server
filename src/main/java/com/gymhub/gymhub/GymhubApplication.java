package com.gymhub.gymhub;

import com.gymhub.gymhub.actions.AddThreadAction;
import com.gymhub.gymhub.actions.AddUserAction;
import com.gymhub.gymhub.actions.MustLogAction;
import com.gymhub.gymhub.components.AiHandler;
import com.gymhub.gymhub.components.Stream;
import com.gymhub.gymhub.domain.ForumAccount;

import com.gymhub.gymhub.domain.Member;
import com.gymhub.gymhub.domain.Post;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.dto.AiRequestBody;
import com.gymhub.gymhub.dto.ThreadCategoryEnum;
import com.gymhub.gymhub.dto.ToxicStatusEnum;
import com.gymhub.gymhub.helper.MemberSequence;
import com.gymhub.gymhub.helper.PostSequence;
import com.gymhub.gymhub.helper.ThreadSequence;
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
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;



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
	@Autowired
	private AiHandler aiHandler;
	@Autowired
	private PostSequence postSequence;
	@Autowired
	private ThreadSequence threadSequence;
	@Autowired
	private MemberSequence memberSequence;


	public static final String LOG_FILE_PATH = "src/main/resources/logs/cache-actions.log";

	public static void main(String[] args) {
		SpringApplication.run(GymhubApplication.class, args);
	}


	//later on: replace cacheFill with InMemoryRepositiory.restoreFromLog


	//TODO Write a post construct method that read from the log and fill in the cache by calling the corresponding methods



	@PostConstruct
	private void cacheFill() throws IOException {
		System.out.println("Duong hello test ");
		List<Member> members = memberRepository.findAll();
		Iterator<Member> iterator = members.iterator();
		while(iterator.hasNext()){
			inMemoryRepository.addUserToCache (iterator.next().getId());
		}
		readAction();

		List<Thread> threads = threadRepository.findAll();
		Iterator<Thread> iterator2 = threads.iterator();
		while(iterator2.hasNext()){
			Thread thread = iterator2.next();
			inMemoryRepository.addThreadToCache(thread.getId(), thread.getCategory(), thread.getCreationDateTime(),ToxicStatusEnum.NOT_TOXIC, thread.getOwner().getId(), false, "");
		}
		readAction();


		List<Post> posts = postRepository.findAll();
		Iterator<Post> iterator3 = posts.iterator();
		while(iterator3.hasNext()){
			Post post = iterator3.next();
			inMemoryRepository.addPostToCache(post.getThread().getId(), post.getId(), post.getAuthor().getId(),  ToxicStatusEnum.NOT_TOXIC, false, "");
		}
		System.out.println("Cache Initialization: Done");
		System.out.println("Swagger UI is available at http://localhost:8080/swagger-ui/index.html");

	}

	@PostConstruct
	private void restoreCache(){
		inMemoryRepository.restoreFromLog();

	}

	private void readAction() {
		System.out.println("Starting read action");

		// Ensure the file exists and is not empty
		File logFile = new File(LOG_FILE_PATH);
		if (!logFile.exists() || logFile.length() == 0) {
			System.out.println("Log file is empty or does not exist.");
			return;
		}

		try (ObjectInputStream ios = new ObjectInputStream(new FileInputStream(logFile))) {
			while (true) {
				try {
					MustLogAction obj = (MustLogAction) ios.readObject();
					System.out.println("Deserialized: " + obj);
				} catch (EOFException e) {
					System.out.println("Finished read action");
					break; // Break the loop on reaching the end of the file
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}



