package com.onelineaday.android.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface JournalDao {
    @Query("SELECT * FROM entries WHERE date = :date")
    JournalEntryEntity getEntryByDate(String date);

    @Query("SELECT * FROM journal_entries")
    List<JournalEntryEntity> getAllEntries();

    @Query("SELECT * FROM journal_entries WHERE date LIKE :pattern")
    List<JournalEntryEntity> getEntriesByPattern(String pattern);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<JournalEntryEntity> entries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JournalEntryEntity entry);

    @Query("DELETE FROM entries")
    void clearAll();
}
