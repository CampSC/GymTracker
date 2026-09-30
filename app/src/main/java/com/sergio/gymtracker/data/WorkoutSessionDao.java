package com.sergio.gymtracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkoutSessionDao {

    @Insert
    long insert(WorkoutSessionEntity session);

    @Update
    void update(WorkoutSessionEntity session);

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    LiveData<List<WorkoutSessionEntity>> observeAll();

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    LiveData<WorkoutSessionEntity> observeById(long sessionId);

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    WorkoutSessionEntity getById(long sessionId);

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    void deleteById(long sessionId);
}