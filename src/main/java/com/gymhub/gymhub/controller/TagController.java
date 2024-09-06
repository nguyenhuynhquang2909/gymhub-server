package com.gymhub.gymhub.controller;

import com.gymhub.gymhub.domain.Tag;
import com.gymhub.gymhub.dto.TagResponseDTO;
import com.gymhub.gymhub.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag request handler", description = "Handlers for tage-related requests")
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;



    @GetMapping("/allTags")
    public List<TagResponseDTO> getAllTags() {
        try {
            return tagService.getAllTags();
        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace for debugging
            return (List<TagResponseDTO>) new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 error if something goes wrong
        }
    }



    @PostMapping("/create")
    public ResponseEntity<?> createTag(@RequestParam String tagName) {
        try {
            Tag createdTag = tagService.createTag(tagName);
            return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace for debugging
            return new ResponseEntity<>("An error occurred while creating the tag.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTag(@PathVariable Long id, @RequestParam String newTagName) {
        try {
            Tag updatedTag = tagService.updateTag(id, newTagName);
            return new ResponseEntity<>(updatedTag, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace for debugging
            return new ResponseEntity<>("An error occurred while updating the tag.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace for debugging
            return new ResponseEntity<>("An error occurred while deleting the tag.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
