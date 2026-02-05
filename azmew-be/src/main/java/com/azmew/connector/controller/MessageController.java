package com.azmew.connector.controller;

import com.azmew.connector.model.SocialMessage;
import com.azmew.connector.model.SocialPage;
import com.azmew.connector.repository.SocialMessageRepository;
import com.azmew.connector.repository.SocialPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final SocialMessageRepository messageRepository;
    private final SocialPageRepository pageRepository;

    @GetMapping("/{pageId}")
    public List<SocialMessage> getMessages(@PathVariable UUID pageId) {
        return pageRepository.findById(pageId)
                .map(messageRepository::findBySocialPageOrderByTimestampDesc)
                .orElse(Collections.emptyList());
    }

    @GetMapping("/recent")
    public List<SocialMessage> getRecentMessages() {
        return messageRepository.findAll();
    }
}
