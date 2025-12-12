package com.onelineaday.controller;

import com.onelineaday.model.JournalEntry;
import com.onelineaday.model.User;
import com.onelineaday.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService service;

    @GetMapping("/entries")
    public ResponseEntity<JournalEntryDTO> getEntry(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getEntry(user, date)
                .map(this::mapToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/entries")
    public ResponseEntity<JournalEntryDTO> updateEntry(
            @AuthenticationPrincipal User user,
            @RequestBody @jakarta.validation.Valid EntryRequest request) { // Added @Valid
        JournalEntry saved = service.saveOrUpdateEntry(user, request.getDate(), request.getText());
        return ResponseEntity.ok(mapToDTO(saved));
    }

    @DeleteMapping("/entries")
    public ResponseEntity<Void> deleteEntry(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        service.deleteEntry(user, date);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<JournalEntryDTO>> getTimeline(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<JournalEntry> timeline = service.getTimeline(user, date);
        List<JournalEntryDTO> dtos = timeline.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    private JournalEntryDTO mapToDTO(JournalEntry entry) {
        return JournalEntryDTO.builder()
                .id(entry.getId())
                .date(entry.getDate())
                .text(entry.getText())
                .year(entry.getDate().getYear())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class JournalEntryDTO {
        private UUID id;
        private LocalDate date;
        private String text;
        private int year;
    }

    @lombok.Data
    public static class EntryRequest {
        @jakarta.validation.constraints.NotNull(message = "Date is required")
        private LocalDate date;

        @jakarta.validation.constraints.NotBlank(message = "Text cannot be empty")
        @jakarta.validation.constraints.Size(max = 500, message = "Text must be under 500 characters")
        private String text;
    }

    // --- New Endpoints ---
    private final com.onelineaday.service.SearchService searchService;
    private final com.onelineaday.service.StatsService statsService;

    @GetMapping("/search")
    public ResponseEntity<List<JournalEntryDTO>> search(
            @AuthenticationPrincipal User user,
            @RequestParam String query) {
        List<JournalEntry> results = searchService.searchEntries(user, query);
        return ResponseEntity.ok(results.stream().map(this::mapToDTO).toList());
    }

    @GetMapping("/stats")
    public ResponseEntity<com.onelineaday.service.StatsService.StatsSummary> getStats(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(statsService.calculateStats(user));
    }
}
