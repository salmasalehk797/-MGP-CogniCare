package com.example.cognicare;

import android.app.Application;

import androidx.lifecycle.LiveData;

public class YourRepository {

    private UserDao dao;
    private LiveData<Boolean> isDatabaseEmpty;

    public YourRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        dao = database.UserDao();
        isDatabaseEmpty = dao.getIsDatabaseEmpty();
    }

    public LiveData<Boolean> getIsDatabaseEmpty() {
        return isDatabaseEmpty;
    }
}