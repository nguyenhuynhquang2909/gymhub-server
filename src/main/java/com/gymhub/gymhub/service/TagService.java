package com.gymhub.gymhub.service;

import com.gymhub.gymhub.domain.Tag;
import com.gymhub.gymhub.domain.Thread;
import com.gymhub.gymhub.repository.TagRepository;
import com.gymhub.gymhub.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ThreadRepository threadRepository;

    public Tag createTag(String tagName) {
        Tag tag = new Tag(tagName);
        return tagRepository.save(tag);
    }

    public Tag updateTag(Long tagId, String newTagName) {
        Optional<Tag> tagOptional = tagRepository.findById(tagId);
        if (tagOptional.isPresent()) {
            Tag tag = tagOptional.get();
            tag.setTagName(newTagName);
            return tagRepository.save(tag);
        }
        throw new RuntimeException("Tag not found");
    }

    public void deleteTag(Long tagId) {
        tagRepository.deleteById(tagId);
    }



    public void addTagToThread(Long threadId, String tagName) {
        Optional<Thread> threadOptional = threadRepository.findById(threadId);
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("Thread not found");
        }

        Thread thread = threadOptional.get();
        Tag tag = tagRepository.findByTagName(tagName);
        if (tag == null) {
            throw new RuntimeException("Tag not found");
        }

        thread.getTags().add(tag);
        threadRepository.save(thread);
    }

    public void deleteTagFromThread(Long threadId, String tagName) {
        Optional<Thread> threadOptional = threadRepository.findById(threadId);
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("Thread not found");
        }

        Thread thread = threadOptional.get();
        Tag tag = tagRepository.findByTagName(tagName);
        if (tag == null) {
            throw new RuntimeException("Tag not found");
        }

        thread.getTags().remove(tag);
        threadRepository.save(thread);
    }
}

