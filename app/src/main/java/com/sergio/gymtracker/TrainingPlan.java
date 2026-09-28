package com.sergio.gymtracker;

import java.util.ArrayList;

public class TrainingPlan implements java.io.Serializable {

    private int id;
    private String name;
    private boolean active;

    private ArrayList<PlanDay> days;

    public TrainingPlan(int id, String name) {
        this.id = id;
        this.name = name;
        this.active = false;

        this.days = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public ArrayList<PlanDay> getDays() {
        return days;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addDay(PlanDay day) {
        days.add(day);
    }

    public void removeDay(PlanDay day) {
        days.remove(day);
    }
}