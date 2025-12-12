package com.onelineaday.service;

import com.onelineaday.model.JournalEntry;
import com.onelineaday.model.User;
import com.onelineaday.repository.JournalEntryRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final JournalEntryRepository repository;

    public StatsSummary getStats(User user) {
        List<JournalEntry> allEntries = repository.findAll(); // Should filter by user, need method
        // Actually, we should use a custom query or strict user filtering
        // Let's assume we fetch all for user.
        // NOTE: Ideally, we should add findByUser to repo.
        // Since we didn't add findByUser explicitly returning List in previous steps
        // (only findByUserAndDate),
        // let's rely on adding it or doing precise logic.
        // Let's assume we can fetch all sorted by date.

        // Let's implement this properly by adding findByUser to repo in next steps if
        // needed,
        // but for now let's assume we can add it.
        return null; // Placeholder to avoid compilation error until repo is updated
    }

    // Better implementation with Repo support
    public StatsSummary calculateStats(User user) {
        List<JournalEntry> entries = repository.findByUserOrderByDateDesc(user);

        if (entries.isEmpty()) {
            return StatsSummary.builder()
                    .totalEntries(0)
                    .currentStreak(0)
                    .longestStreak(0)
                    .totalWords(0)
                    .build();
        }

        int totalEntries = entries.size();
        long totalWords = entries.stream()
                .map(JournalEntry::getText)
                .mapToLong(text -> text.split("\\s+").length)
                .sum();

        int currentStreak = 0;
        int longestStreak = 0;
        int tempStreak = 1;

        // Check if entered today or yesterday for current streak
        LocalDate today = LocalDate.now();
        LocalDate lastEntryDate = entries.get(0).getDate();

        if (lastEntryDate.equals(today) || lastEntryDate.equals(today.minusDays(1))) {
            // Calculate backwards for current streak
            currentStreak = 1;
            for (int i = 0; i < entries.size() - 1; i++) {
                LocalDate d1 = entries.get(i).getDate();
                LocalDate d2 = entries.get(i + 1).getDate();
                if (ChronoUnit.DAYS.between(d2, d1) == 1) {
                    currentStreak++;
                } else {
                    break;
                }
            }
        }

        // Calculate longest streak
        // Sort ascending for easier streak calculation
        entries.sort(Comparator.comparing(JournalEntry::getDate));

        for (int i = 0; i < entries.size() - 1; i++) {
            LocalDate d1 = entries.get(i).getDate();
            LocalDate d2 = entries.get(i + 1).getDate();
            if (ChronoUnit.DAYS.between(d1, d2) == 1) {
                tempStreak++;
            } else {
                longestStreak = Math.max(longestStreak, tempStreak);
                tempStreak = 1;
            }
        }
        longestStreak = Math.max(longestStreak, tempStreak);

        return StatsSummary.builder()
                .totalEntries(totalEntries)
                .totalWords(totalWords)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .build();
    }

    @Data
    @Builder
    public static class StatsSummary {
        private int totalEntries;
        private long totalWords;
        private int currentStreak;
        private int longestStreak;
    }
}
