package com.gymhub.gymhub.repo;
import com.gymhub.gymhub.domain.ForumAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ForumAccount, String> {
}
