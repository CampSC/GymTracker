package com.sergio.gymtracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlanExerciseDao {

    @Query("SELECT * FROM plan_exercises WHERE planDayId = :planDayId ORDER BY position ASC, id ASC")
    LiveData<List<PlanExerciseEntity>> getByDay(long planDayId);

    @Query("SELECT * FROM plan_exercises WHERE planDayId = :planDayId ORDER BY position ASC, id ASC")
    List<PlanExerciseEntity> getByDaySync(long planDayId);

    @Query("SELECT * FROM plan_exercises WHERE id = :id LIMIT 1")
    PlanExerciseEntity getById(long id);

    @Insert
    long insert(PlanExerciseEntity exercise);

    @Insert
    void insertAll(List<PlanExerciseEntity> exercises);

    @Update
    void update(PlanExerciseEntity exercise);

    @Delete
    void delete(PlanExerciseEntity exercise);

    @Query("DELETE FROM plan_exercises WHERE planDayId = :planDayId")
    void deleteByDay(long planDayId);

    @Query("UPDATE plan_exercises SET position = :position WHERE id = :id")
    void updatePosition(long id, int position);
}