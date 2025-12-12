package com.onelineaday.android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.onelineaday.android.db.JournalEntryEntity;
import com.onelineaday.android.db.JournalDao;

@Database(entities = { JournalEntryEntity.class }, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JournalDao journalDao();
}
