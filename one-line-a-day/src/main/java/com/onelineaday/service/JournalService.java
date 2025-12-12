package com.onelineaday.service;

import com.onelineaday.model.JournalEntry;
import com.onelineaday.model.User;
import com.onelineaday.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalEntryRepository repository;

    @Transactional
    public JournalEntry saveOrUpdateEntry(User user, LocalDate date, String text) {
        Optional<JournalEntry> existing = repository.findByUserAndDate(user, date);
        if (existing.isPresent()) {
            JournalEntry entry = existing.get();
            entry.setText(text);
            return repository.save(entry);
        } else {
            JournalEntry entry = JournalEntry.builder()
                    .user(user)
                    .date(date)
                    .text(text)
                    .build();
            return repository.save(entry);
        }
    }

    public Optional<JournalEntry> getEntry(User user, LocalDate date) {
        return repository.findByUserAndDate(user, date);
    }

    public List<JournalEntry> getTimeline(User user, LocalDate referenceDate) {
        return repository.findTimelineForDate(user, referenceDate.getMonthValue(), referenceDate.getDayOfMonth());
    }

    public void deleteEntry(User user, LocalDate date) {
        repository.findByUserAndDate(user, date).ifPresent(repository::delete);
    }
}
