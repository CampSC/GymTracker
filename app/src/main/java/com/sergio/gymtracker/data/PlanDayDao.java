package com.sergio.gymtracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlanDayDao {

    @Query(
            "SELECT * FROM plan_days " +
                    "WHERE planId = :planId " +
                    "ORDER BY dayNumber ASC"
    )
    LiveData<List<PlanDayEntity>> observeByPlan(long planId);

    @Query("SELECT * FROM plan_days WHERE id = :id LIMIT 1")
    PlanDayEntity getById(long id);

    @Insert
    long insert(PlanDayEntity day);

    @Update
    void update(PlanDayEntity day);

    @Delete
    void delete(PlanDayEntity day);
}