package com.onelineaday.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AnalyticsService {

    public void trackEvent(String userId, String event, Map<String, Object> metadata) {
        // In a real app, send to PostHog / Mixpanel / BigQuery
        // Here we just log it as requested
        log.info("ANALYTICS [{}]: User={} Meta={}", event, userId, metadata);
    }
}
