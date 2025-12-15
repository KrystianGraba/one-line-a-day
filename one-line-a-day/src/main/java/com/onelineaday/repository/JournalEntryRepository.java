package com.onelineaday.repository;

import com.onelineaday.model.JournalEntry;
import com.onelineaday.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    Optional<JournalEntry> findByUserAndDate(User user, LocalDate date);

    // Timeline query: Find entries for same Month/Day across years
    @Query("SELECT j FROM JournalEntry j WHERE j.user = :user AND EXTRACT(MONTH FROM j.date) = :month AND EXTRACT(DAY FROM j.date) = :day ORDER BY j.date DESC")
    List<JournalEntry> findTimelineForDate(@Param("user") User user, @Param("month") int month, @Param("day") int day);

    // Check which days in a month have entries (for calendar view)
    @Query("SELECT j.date FROM JournalEntry j WHERE j.user = :user AND EXTRACT(YEAR FROM j.date) = :year AND EXTRACT(MONTH FROM j.date) = :month")
    List<LocalDate> findDatesWithEntries(@Param("user") User user, @Param("year") int year, @Param("month") int month);

    @Query("SELECT j FROM JournalEntry j WHERE j.user = :user AND LOWER(j.text) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY j.date DESC")
    List<JournalEntry> searchByText(@Param("user") User user, @Param("query") String query);

    List<JournalEntry> findByUserOrderByDateDesc(User user);
}
