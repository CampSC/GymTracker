
package com.sergio.gymtracker;

import java.util.ArrayList;

public class PlanDay implements java.io.Serializable {

    private int id;
    private int dayNumber;
    private String name;
    private boolean restDay;

    private ArrayList<PlanExercise> exercises;

    public PlanDay(int id, int dayNumber, String name, boolean restDay) {
        this.id = id;
        this.dayNumber = dayNumber;
        this.name = name;
        this.restDay = restDay;

        this.exercises = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getName() {
        return name;
    }

    public boolean isRestDay() {
        return restDay;
    }

    public ArrayList<PlanExercise> getExercises() {
        return exercises;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRestDay(boolean restDay) {
        this.restDay = restDay;
    }

    public void addExercise(PlanExercise exercise) {
        exercises.add(exercise);
    }

    public void removeExercise(PlanExercise exercise) {
        exercises.remove(exercise);
    }
}