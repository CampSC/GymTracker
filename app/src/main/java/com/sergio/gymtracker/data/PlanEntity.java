package com.sergio.gymtracker.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "plans")
public class PlanEntity {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_READY = "READY";
    public static final String STATUS_ACTIVE = "ACTIVE";

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public boolean active;
    public String status;
    public boolean savedAsTemplate;

    // Construtor antigo, mantido para compatibilidade com o código existente
    @Ignore
    public PlanEntity(String name, boolean active) {
        this(name, active, active ? STATUS_ACTIVE : STATUS_READY, false);
    }

    // Construtor principal utilizado pelo Room
    public PlanEntity(String name, boolean active, String status, boolean savedAsTemplate) {
        this.name = name;
        this.active = active;
        this.status = status;
        this.savedAsTemplate = savedAsTemplate;
    }
}