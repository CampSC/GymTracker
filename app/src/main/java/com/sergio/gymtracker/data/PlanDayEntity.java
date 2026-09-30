package com.sergio.gymtracker.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "plan_days",
        foreignKeys = @ForeignKey(
                entity = PlanEntity.class,
                parentColumns = "id",
                childColumns = "planId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("planId")}
)
public class PlanDayEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long planId;

    public int dayNumber;

    public String name;

    public boolean restDay;

    public PlanDayEntity(
            long planId,
            int dayNumber,
            String name,
            boolean restDay
    ) {
        this.planId = planId;
        this.dayNumber = dayNumber;
        this.name = name;
        this.restDay = restDay;
    }
}