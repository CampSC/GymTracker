package com.sergio.gymtracker.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
        tableName = "plan_exercises",
        foreignKeys = {
                @ForeignKey(
                        entity = PlanDayEntity.class,
                        parentColumns = "id",
                        childColumns = "planDayId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("planDayId")}
)
public class PlanExerciseEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long planDayId;
    public String name;
    public int sets;
    public int reps;
    public double weight;
    public String notes;
    public int position;

    // Construtor antigo, mantido para compatibilidade
    @Ignore
    public PlanExerciseEntity(
            long planDayId,
            String name,
            int sets,
            int reps,
            double weight,
            String notes
    ) {
        this(planDayId, name, sets, reps, weight, notes, 0);
    }

    // Construtor completo
    public PlanExerciseEntity(
            long planDayId,
            String name,
            int sets,
            int reps,
            double weight,
            String notes,
            int position
    ) {
        this.planDayId = planDayId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.notes = notes;
        this.position = position;
    }
}