package com.example.cognicare;

import android.app.Application;

import androidx.room.Room;

public class MyApp extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this, AppDatabase.class, "my-database")
                .allowMainThreadQueries() // For simplicity (not recommended for production)
                .build();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}
