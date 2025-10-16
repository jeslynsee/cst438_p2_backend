package com.example.demo.workout;

import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    boolean existsByWorkoutPlanName(@Valid String name, String planName);
    Optional<Workout> findByWorkoutId(@Valid Long userId);
    List<Workout> findByUserIdAndWorkoutPlanNameAndDay(Long userId, String workoutPlanName, String day);
    Optional<Workout> findByUserIdAndWorkoutPlanNameAndExerciseName(Long userId, String workoutPlanName, String exerciseName);
    boolean existsByUserIdAndWorkoutPlanName(Long userId, String workoutPlanName);
    void deleteByUserIdAndWorkoutPlanName(Long userId, String workoutPlanName);
}
