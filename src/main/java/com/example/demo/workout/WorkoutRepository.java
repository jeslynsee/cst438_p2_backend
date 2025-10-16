package com.example.demo.workout;

import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    // boolean existsByWorkoutPlanName(@Valid String name, String planName);
    //Optional<Workout> findByWorkoutId(@Valid String userId);
    List<Workout> findByUserIdAndWorkoutPlanNameAndDay(String userId, String workoutPlanName, String day);
    Optional<Workout> findByUserIdAndWorkoutPlanNameAndExerciseName(String userId, String workoutPlanName, String exerciseName);
    boolean existsByUserIdAndWorkoutPlanName(String userId, String workoutPlanName);
    void deleteByUserIdAndWorkoutPlanName(String userId, String workoutPlanName);
}
