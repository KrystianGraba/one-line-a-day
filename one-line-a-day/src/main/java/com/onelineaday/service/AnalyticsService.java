package com.onelineaday.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@lombok.RequiredArgsConstructor
public class AnalyticsService {

    private final com.onelineaday.repository.AnalyticsEventRepository repository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public void trackEvent(String userIdStr, String event, Map<String, Object> metadata) {
        // Log it as requested
        log.info("ANALYTICS [{}]: User={} Meta={}", event, userIdStr, metadata);

        try {
            java.util.UUID userId = null;
            if (userIdStr != null && !userIdStr.equals("anonymous")) {
                try {
                    userId = java.util.UUID.fromString(userIdStr);
                } catch (IllegalArgumentException e) {
                    // ignore invalid UUIDs
                }
            }

            String metaJson = "{}";
            if (metadata != null) {
                metaJson = objectMapper.writeValueAsString(metadata);
            }

            com.onelineaday.model.AnalyticsEvent entity = com.onelineaday.model.AnalyticsEvent.builder()
                    .event(event)
                    .userId(userId)
                    .metadata(metaJson)
                    .build();

            repository.save(entity);
        } catch (Exception e) {
            log.error("Failed to save analytics event", e);
        }
    }
}
