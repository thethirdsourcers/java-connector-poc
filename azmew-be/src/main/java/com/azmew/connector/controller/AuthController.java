package com.azmew.connector.controller;

import com.azmew.connector.model.SocialPage;
import com.azmew.connector.model.Tenant;
import com.azmew.connector.repository.SocialPageRepository;
import com.azmew.connector.repository.TenantRepository;
import com.azmew.connector.service.MetaService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final Dotenv dotenv = Dotenv.load();
    private final MetaService metaService;
    private final SocialPageRepository pageRepository;
    private final TenantRepository tenantRepository;

    @GetMapping("/facebook")
    public RedirectView facebookAuth(@RequestParam(defaultValue = "demo-tenant-1") String tenantId) {
        String appId = dotenv.get("FACEBOOK_APP_ID");
        String redirectUri = dotenv.get("FACEBOOK_REDIRECT_URI");
        String scope = "pages_show_list,pages_messaging,instagram_basic,instagram_manage_messages,public_profile";
        String state = tenantId + ":facebook";

        String authUrl = String.format(
                "https://www.facebook.com/v18.0/dialog/oauth?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                appId, 
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(scope, StandardCharsets.UTF_8),
                URLEncoder.encode(state, StandardCharsets.UTF_8)
        );

        log.info("Redirecting to Facebook Auth: {}", authUrl);
        return new RedirectView(authUrl);
    }

    @GetMapping("/tiktok")
    public RedirectView tiktokAuth(@RequestParam(defaultValue = "demo-tenant-1") String tenantId) {
        String clientKey = dotenv.get("TIKTOK_APP_ID");
        String redirectUri = dotenv.get("TIKTOK_REDIRECT_URI");
        String state = tenantId + ":tiktok";
        String scope = "user.info.basic,video.list,external_id,advertiser.info,advertiser.video.list,message.direct.send";

        String authUrl = String.format(
                "https://www.tiktok.com/v2/auth/authorize/?client_key=%s&scope=%s&response_type=code&redirect_uri=%s&state=%s",
                clientKey,
                URLEncoder.encode(scope, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(state, StandardCharsets.UTF_8)
        );

        log.info("Redirecting to TikTok Auth: {}", authUrl);
        return new RedirectView(authUrl);
    }

    @GetMapping("/facebook/callback")
    public String facebookCallback(@RequestParam(required = false) String code, @RequestParam(required = false) String state) {
        if (code == null) return "Error: No code received";
        
        String tenantApiKey = (state != null && state.contains(":")) ? state.split(":")[0] : "demo-tenant-1";
        
        metaService.exchangeCodeForToken(code)
                .flatMap(response -> {
                    String userToken = (String) response.get("access_token");
                    return metaService.getPages(userToken);
                })
                .subscribe(pagesResponse -> {
                    List<Map> pages = (List<Map>) pagesResponse.get("data");
                    if (pages != null) {
                        Tenant tenant = tenantRepository.findByApiKey(tenantApiKey)
                                .orElseGet(() -> tenantRepository.save(Tenant.builder()
                                        .apiKey(tenantApiKey)
                                        .name("Auto-Created Tenant")
                                        .build()));

                        for (Map pageMap : pages) {
                            String pageId = (String) pageMap.get("id");
                            String pageName = (String) pageMap.get("name");
                            String pageToken = (String) pageMap.get("access_token");

                            SocialPage page = pageRepository.findByPageIdAndPlatform(pageId, "FACEBOOK")
                                    .orElse(SocialPage.builder()
                                            .pageId(pageId)
                                            .platform("FACEBOOK")
                                            .tenant(tenant)
                                            .build());

                            page.setPageName(pageName);
                            page.setAccessToken(pageToken);
                            pageRepository.save(page);
                            log.info("Saved/Updated Facebook Page: {}", pageName);
                        }
                    }
                });

        return "Facebook authentication complete! Check your dashboard. (Close this window)";
    }

    @GetMapping("/tiktok/callback")
    public String tiktokCallback(@RequestParam(required = false) String code, @RequestParam(required = false) String state) {
        if (code == null) return "Error: No code received";
        log.info("TikTok callback received. Code: {}, State: {}", code, state);
        return "TikTok authenticated! Processing logic coming soon...";
    }
}
