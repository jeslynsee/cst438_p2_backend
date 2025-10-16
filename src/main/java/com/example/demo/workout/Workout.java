package com.example.demo.workout;

import jakarta.persistence.*;

@Entity
@Table(name = "workout", uniqueConstraints = {
    @UniqueConstraint(name = "uk_plan_user_name",
            columnNames = {"user_id", "workout_plan_name", "exercise_name", "day"}
    )
})
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private String userId; // if you’re not using User table, keep as String to match your TS code

    @Column(name = "workout_plan_name", nullable = false)
    private String workoutPlanName;

    @Column(name = "exercise_name", nullable = false)
    private String exerciseName;

    @Column(nullable = false)
    private String day;

    @Column(nullable = false)
    private int sets;

    @Column(nullable = false)
    private int reps;

    public Workout() {}
    public Workout(String userId, String workoutPlanName, String exerciseName, String day, int sets, int reps) {
        this.userId = userId;
        this.workoutPlanName = workoutPlanName;
        this.exerciseName = exerciseName;
        this.day = day;
        this.sets = sets;
        this.reps = reps;
    }

    // getters/setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkoutPlanName() {
        return workoutPlanName;
    }

    public void setWorkoutPlanName(String workoutPlanName) {
        this.workoutPlanName = workoutPlanName;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }
}
