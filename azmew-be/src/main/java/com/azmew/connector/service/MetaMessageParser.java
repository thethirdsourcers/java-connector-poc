package com.azmew.connector.service;

import com.azmew.connector.model.SocialMessage;
import com.azmew.connector.model.SocialPage;
import com.azmew.connector.repository.SocialMessageRepository;
import com.azmew.connector.repository.SocialPageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetaMessageParser {

    private final SocialPageRepository pageRepository;
    private final SocialMessageRepository messageRepository;

    public void processWebhookPayload(Map<String, Object> payload) {
        try {
            List<Map> entries = (List<Map>) payload.get("entry");
            if (entries == null || entries.isEmpty()) {
                log.warn("No entries in webhook payload");
                return;
            }

            for (Map entry : entries) {
                List<Map> messaging = (List<Map>) entry.get("messaging");
                if (messaging != null) {
                    processFacebookMessages(messaging);
                }

                List<Map> changes = (List<Map>) entry.get("changes");
                if (changes != null) {
                    processInstagramMessages(changes);
                }
            }
        } catch (Exception e) {
            log.error("Error processing webhook payload", e);
        }
    }

    private void processFacebookMessages(List<Map> messaging) {
        for (Map msg : messaging) {
            try {
                Map sender = (Map) msg.get("sender");
                Map recipient = (Map) msg.get("recipient");
                Map message = (Map) msg.get("message");
                
                if (message == null) continue;

                String senderId = (String) sender.get("id");
                String recipientId = (String) recipient.get("id");
                String messageId = (String) message.get("mid");
                String text = (String) message.get("text");
                Long timestamp = ((Number) msg.get("timestamp")).longValue();

                // Find the social page
                SocialPage page = pageRepository.findByPageIdAndPlatform(recipientId, "FACEBOOK")
                        .orElse(null);

                if (page == null) {
                    log.warn("Page not found for recipient: {}", recipientId);
                    continue;
                }

                // Check if message already exists
                if (messageRepository.findByExternalMessageId(messageId).isPresent()) {
                    log.debug("Message already exists: {}", messageId);
                    continue;
                }

                // Create and save message
                SocialMessage socialMessage = SocialMessage.builder()
                        .senderId(senderId)
                        .senderName("Facebook User " + senderId.substring(0, 8))
                        .content(text != null ? text : "[Media/Attachment]")
                        .platform("FACEBOOK")
                        .externalMessageId(messageId)
                        .socialPage(page)
                        .isFromUser(false)
                        .timestamp(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(timestamp),
                                ZoneId.systemDefault()))
                        .build();

                messageRepository.save(socialMessage);
                log.info("Saved Facebook message: {} from {}", messageId, senderId);

            } catch (Exception e) {
                log.error("Error processing Facebook message", e);
            }
        }
    }

    private void processInstagramMessages(List<Map> changes) {
        for (Map change : changes) {
            try {
                Map value = (Map) change.get("value");
                if (value == null) continue;

                String field = (String) change.get("field");
                if (!"messages".equals(field)) continue;

                Map message = (Map) value.get("message");
                if (message == null) continue;

                String senderId = (String) value.get("from");
                String recipientId = (String) value.get("to");
                String messageId = (String) message.get("mid");
                String text = (String) message.get("text");

                // Find Instagram page
                SocialPage page = pageRepository.findByPageIdAndPlatform(recipientId, "INSTAGRAM")
                        .orElse(null);

                if (page == null) {
                    log.warn("Instagram page not found for recipient: {}", recipientId);
                    continue;
                }

                if (messageRepository.findByExternalMessageId(messageId).isPresent()) {
                    continue;
                }

                SocialMessage socialMessage = SocialMessage.builder()
                        .senderId(senderId)
                        .senderName("IG User " + senderId.substring(0, 8))
                        .content(text != null ? text : "[Media]")
                        .platform("INSTAGRAM")
                        .externalMessageId(messageId)
                        .socialPage(page)
                        .isFromUser(false)
                        .timestamp(LocalDateTime.now())
                        .build();

                messageRepository.save(socialMessage);
                log.info("Saved Instagram message: {}", messageId);

            } catch (Exception e) {
                log.error("Error processing Instagram message", e);
            }
        }
    }
}
