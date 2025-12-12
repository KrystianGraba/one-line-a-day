package com.onelineaday.controller;

import com.onelineaday.model.User;
import com.onelineaday.service.AnalyticsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping
    public ResponseEntity<Void> trackEvent(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid AnalyticsEventRequest request) {
        analyticsService.trackEvent(user.getId().toString(), request.getEvent(), request.getMetadata());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class AnalyticsEventRequest {
        @NotBlank
        private String event;
        private Map<String, Object> metadata;
    }
}
