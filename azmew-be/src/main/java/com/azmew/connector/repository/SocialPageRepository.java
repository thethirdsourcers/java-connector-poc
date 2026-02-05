package com.azmew.connector.repository;

import com.azmew.connector.model.SocialPage;
import com.azmew.connector.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface SocialPageRepository extends JpaRepository<SocialPage, UUID> {
    List<SocialPage> findByTenant(Tenant tenant);
    Optional<SocialPage> findByPageIdAndPlatform(String pageId, String platform);
}
