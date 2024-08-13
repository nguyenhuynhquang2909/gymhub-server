package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.ModeratorRequestAndResponseDTO;
import com.gymhub.gymhub.dto.PostResponseDTO;
import com.gymhub.gymhub.dto.ThreadResponseDTO;
import com.gymhub.gymhub.repository.InMemoryRepository;
import com.gymhub.gymhub.repository.ModeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModService {

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Autowired
    private InMemoryRepository inMemoryRepository;


    public ResponseEntity<Void> updateModInfo(ModeratorRequestAndResponseDTO modDTO) {
        // Check if mod exists
        Optional<Moderator> mod = moderatorRepository.findModByUserName(modDTO.getUsername());
        if (mod.isPresent()) {
            Moderator existingMod = mod.get();
            if (!existingMod.getUserName().startsWith("mod")) {
                throw new IllegalArgumentException("Moderator username must start with 'mod'");
            }
            existingMod.setPassword(modDTO.getPassword());
            existingMod.setEmail(modDTO.getEmail());
            moderatorRepository.save(existingMod);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all pending posts
    public List<PostResponseDTO> getAllPendingPosts() {
        return (List<PostResponseDTO>) inMemoryRepository.getPendingPosts();
    }

    // Get all pending threads
    public List<ThreadResponseDTO> getAllPendingThreads() {
        return (List<ThreadResponseDTO>) inMemoryRepository.getPendingThreads();
    }
}
