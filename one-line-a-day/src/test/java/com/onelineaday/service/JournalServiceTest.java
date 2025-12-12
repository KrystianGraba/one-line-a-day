package com.onelineaday.service;

import com.onelineaday.model.JournalEntry;
import com.onelineaday.model.User;
import com.onelineaday.repository.JournalEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalEntryRepository repository;

    @InjectMocks
    private JournalService service;

    @Test
    void saveOrUpdateEntry_NewEntry_ShouldSave() {
        User user = new User();
        user.setId(UUID.randomUUID());
        LocalDate today = LocalDate.now();
        String text = "My day was great";

        when(repository.findByUserAndDate(user, today)).thenReturn(Optional.empty());
        when(repository.save(any(JournalEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JournalEntry result = service.saveOrUpdateEntry(user, today, text);

        assertNotNull(result);
        assertEquals(text, result.getText());
        assertEquals(today, result.getDate());
        verify(repository).save(any(JournalEntry.class));
    }

    @Test
    void saveOrUpdateEntry_ExistingEntry_ShouldUpdate() {
        User user = new User();
        user.setId(UUID.randomUUID());
        LocalDate today = LocalDate.now();
        JournalEntry existing = JournalEntry.builder()
                .id(UUID.randomUUID())
                .user(user)
                .date(today)
                .text("Old text")
                .build();

        when(repository.findByUserAndDate(user, today)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        JournalEntry result = service.saveOrUpdateEntry(user, today, "New text");

        assertEquals("New text", result.getText());
        assertEquals(existing.getId(), result.getId());
    }
}
