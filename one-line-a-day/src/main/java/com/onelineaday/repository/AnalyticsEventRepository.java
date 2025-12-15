package com.onelineaday.repository;

import com.onelineaday.model.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, UUID> {
    List<AnalyticsEvent> findByUserIdOrderByTimestampDesc(UUID userId);

    List<AnalyticsEvent> findByEventOrderByTimestampDesc(String event);

    List<AnalyticsEvent> findByTimestampAfter(LocalDateTime parse);
}
