package com.onelineaday.service;

import com.onelineaday.model.JournalEntry;
import com.onelineaday.model.User;
import com.onelineaday.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final JournalEntryRepository repository;

    public List<JournalEntry> searchEntries(User user, String query) {
        // Simple implementation: Client-side filtering if DB doesn't support full text
        // easily
        // Or better: Use a custom repository method.
        // For MVP without creating complex specification, we will add a method to Repo.
        return repository.searchByText(user, query);
    }
}
