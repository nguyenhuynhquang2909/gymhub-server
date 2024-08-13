package com.gymhub.gymhub.service;

import com.gymhub.gymhub.actions.ChangeThreadStatusAction;
import com.gymhub.gymhub.domain.Moderator;
import com.gymhub.gymhub.dto.*;
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
public class ModService implements UserDetailsService {
    @Autowired
    ModeratorRepository moderatorRepository;
    @Autowired
    InMemoryRepository inMemoryRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Moderator mod = moderatorRepository.findModByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return User.builder()
                .username(mod.getUserName())
                .password(mod.getPassword())
                .roles("USER")
                .build();
    }

    public ResponseEntity<Void> updateModInfo(ModeratorRequestAndResponseDTO modDTO){
        //check if mod exist
        Optional<Moderator> mod = moderatorRepository.findModByUsername(modDTO.getUsername());
        if(mod.isPresent()){
            Moderator existingMod = mod.get();
            existingMod.setPassword(modDTO.getPassword());
            existingMod.setEmail(modDTO.getEmail());
           moderatorRepository.save(existingMod);
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //get all pending post
    public List<PostResponseDTO> getAllPendingPosts(){
      return (List<PostResponseDTO>) inMemoryRepository.getPendingPosts();
    }

    //get all pending thread
    public List<ThreadResponseDTO> getAllPendingThreads(){
        return (List<ThreadResponseDTO>) inMemoryRepository.getPendingThreads();
    }
    //change toxicStatus of a thread


    public boolean changeThreadStatus(long threadId, String category, int from, int to, String reason) {
        boolean result = cache.changeThreadStatus(threadId, category, from, to, reason);
        ChangeThreadStatusAction action = new ChangeThreadStatusAction(++actionIdCounter, threadId, category, from, to, reason);
        logAction(action);
        return result;
    }

    public boolean changeToxicStatusOfAThread(ChangeForumUnitToxicStatusRequestDTO targetThreadDTO){
        int originalStatus = 1; //mod's always receive pending thread
        return inMemoryRepository.changeThreadStatusFromModDashBoard(targetThreadDTO.getId(), targetThreadDTO.getClass()getThreadCategory().name(),
                reportThreadRequestDTO.getFrom(), reportThreadRequestDTO.getTo(), reportThreadRequestDTO.getReason());
    }

    //change toxicStatus of a post
}
