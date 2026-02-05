package com.azmew.connector.repository;

import com.azmew.connector.model.SocialMessage;
import com.azmew.connector.model.SocialPage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface SocialMessageRepository extends JpaRepository<SocialMessage, UUID> {
    List<SocialMessage> findBySocialPageOrderByTimestampDesc(SocialPage socialPage);
    Optional<SocialMessage> findByExternalMessageId(String externalMessageId);
}
