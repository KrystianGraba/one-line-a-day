package com.onelineaday.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "analytics_events")
public class AnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String event; // e.g., "app_opened", "entry_saved"

    private UUID userId; // Nullable (for pre-login events)

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON string or simple text

    @CreationTimestamp
    private LocalDateTime timestamp;
}
