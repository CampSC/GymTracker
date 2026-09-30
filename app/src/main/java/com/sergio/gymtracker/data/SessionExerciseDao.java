package com.sergio.gymtracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SessionExerciseDao {

    @Insert
    long insert(SessionExerciseEntity exercise);

    @Query(
            "SELECT * FROM session_exercises " +
                    "WHERE sessionId = :sessionId ORDER BY position ASC"
    )
    LiveData<List<SessionExerciseEntity>> observeBySession(long sessionId);

    @Query(
            "SELECT * FROM session_exercises " +
                    "WHERE sessionId = :sessionId ORDER BY position ASC"
    )
    List<SessionExerciseEntity> getBySession(long sessionId);

    @Query("SELECT * FROM session_exercises WHERE id = :id")
    SessionExerciseEntity getById(long id);
}