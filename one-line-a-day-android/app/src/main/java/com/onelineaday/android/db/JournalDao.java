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

    @Query("SELECT * FROM entries")
    List<JournalEntryEntity> getAllEntries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<JournalEntryEntity> entries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JournalEntryEntity entry);

    @Query("DELETE FROM entries")
    void clearAll();
}
