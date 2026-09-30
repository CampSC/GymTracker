package com.sergio.gymtracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "exercise_library")
public class ExerciseLibraryEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String muscleGroup;
    public String equipment;
    public boolean custom;
    public boolean favorite;

    public ExerciseLibraryEntity(
            String name,
            String muscleGroup,
            String equipment,
            boolean custom,
            boolean favorite
    ) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.custom = custom;
        this.favorite = favorite;
    }
}