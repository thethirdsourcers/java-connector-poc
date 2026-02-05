package com.azmew.connector.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String senderName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String platform; // FACEBOOK, INSTAGRAM, TIKTOK

    @Column(nullable = false)
    private String externalMessageId;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_page_id", nullable = false)
    private SocialPage socialPage;

    @Column(nullable = false)
    @Builder.Default
    private boolean isFromUser = false; // False = Incoming from customer, True = Outgoing from Azmew

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
