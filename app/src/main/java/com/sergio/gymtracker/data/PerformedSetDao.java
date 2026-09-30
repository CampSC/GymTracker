package com.sergio.gymtracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PerformedSetDao {

    @Insert
    long insert(PerformedSetEntity set);

    @Update
    void update(PerformedSetEntity set);

    @Query(
            "SELECT * FROM performed_sets " +
                    "WHERE sessionExerciseId = :exerciseId ORDER BY setNumber ASC"
    )
    LiveData<List<PerformedSetEntity>> observeByExercise(long exerciseId);

    @Query(
            "SELECT * FROM performed_sets " +
                    "WHERE sessionExerciseId = :exerciseId ORDER BY setNumber ASC"
    )
    List<PerformedSetEntity> getByExercise(long exerciseId);

    @Query("DELETE FROM performed_sets WHERE id = :setId")
    void deleteById(long setId);
}