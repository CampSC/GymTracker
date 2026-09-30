package com.sergio.gymtracker.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "performed_sets",
        foreignKeys = {
                @ForeignKey(
                        entity = SessionExerciseEntity.class,
                        parentColumns = "id",
                        childColumns = "sessionExerciseId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("sessionExerciseId")}
)
public class PerformedSetEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long sessionExerciseId;

    public int setNumber;
    public int reps;
    public double weight;

    public boolean completed;

    public PerformedSetEntity(
            long sessionExerciseId,
            int setNumber,
            int reps,
            double weight,
            boolean completed
    ) {
        this.sessionExerciseId = sessionExerciseId;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
    }
}