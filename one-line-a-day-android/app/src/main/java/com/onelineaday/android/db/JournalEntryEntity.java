package com.onelineaday.android.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "entries")
public class JournalEntryEntity {
    @PrimaryKey
    @NonNull
    public String date; // Using date as PK since 1 entry per day

    public String text;
    public String id; // Backend ID
    public int year;

    public JournalEntryEntity(@NonNull String date, String text, String id, int year) {
        this.date = date;
        this.text = text;
        this.id = id;
        this.year = year;
    }
}
