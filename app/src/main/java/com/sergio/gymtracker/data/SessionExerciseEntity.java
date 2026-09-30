package com.sergio.gymtracker.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "session_exercises",
        foreignKeys = {
                @ForeignKey(
                        entity = WorkoutSessionEntity.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("sessionId")}
)
public class SessionExerciseEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long sessionId;

    public String exerciseName;

    public int plannedSets;
    public int plannedReps;
    public double plannedWeight;

    public int position;

    public SessionExerciseEntity(
            long sessionId,
            String exerciseName,
            int plannedSets,
            int plannedReps,
            double plannedWeight,
            int position
    ) {
        this.sessionId = sessionId;
        this.exerciseName = exerciseName;
        this.plannedSets = plannedSets;
        this.plannedReps = plannedReps;
        this.plannedWeight = plannedWeight;
        this.position = position;
    }
}