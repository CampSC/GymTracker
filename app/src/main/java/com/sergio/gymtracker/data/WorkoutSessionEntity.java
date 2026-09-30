package com.sergio.gymtracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_sessions")
public class WorkoutSessionEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String planName;
    public String dayName;

    public long startedAt;
    public long finishedAt;

    public String notes;

    public WorkoutSessionEntity(
            String planName,
            String dayName,
            long startedAt
    ) {
        this.planName = planName;
        this.dayName = dayName;
        this.startedAt = startedAt;
        this.finishedAt = 0;
        this.notes = "";
    }
}