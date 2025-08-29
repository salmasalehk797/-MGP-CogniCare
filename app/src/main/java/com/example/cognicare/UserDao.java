package com.example.cognicare;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserEntity yourEntity);

    @Query("SELECT * FROM User LIMIT 1")
    UserEntity getUser();

    @Query("SELECT COUNT(*) FROM User")
    LiveData<Integer> getCount();

    @Query("SELECT CASE WHEN COUNT(*) = 0 THEN 1 ELSE 0 END FROM User")
    LiveData<Boolean> getIsDatabaseEmpty();

    @Query("DELETE FROM User")
    void deleteSetupData();
}

