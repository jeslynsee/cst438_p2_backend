package com.example.demo.workout;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** controller can:
 * getAllWorkouts
 * getWorkoutById
 * getExercisesForDay
 * deleteWorkoutById
 * deleteWorkoutByUserAndPlanName
 * addExercise
 *
 */



@RestController
@RequestMapping("/api/workout")
public class WorkoutController {
    private final WorkoutRepository workout;

    public WorkoutController(WorkoutRepository workout) {
        this.workout = workout;
    }

    @GetMapping
    public List<Workout> getAllWorkouts() {return workout.findAll();}

    @GetMapping("/{id}")
    public Workout getWorkoutById(@PathVariable long id) {   // <-- PathVariable, not RequestParam
        return workout.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteWorkoutById(@PathVariable long id) {
        if (workout.existsById(id)) {        // check if it exists
            workout.deleteById(id);          // delete it
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // 404 if not found
        }
    }

    @DeleteMapping("/delete/{userId}/{planName}")
    public ResponseEntity<Void> deleteWorkoutByUserPlanName(@PathVariable String userId, @PathVariable String planName) {
        if(workout.existsByUserIdAndWorkoutPlanName(userId, planName)){
            workout.deleteByUserIdAndWorkoutPlanName(userId, planName);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();  // 404 if not found
        }
    }

    @PostMapping("/add-exercise")
    public ResponseEntity<Void> addExercise(@RequestBody Workout payload) {
        // optional upsert: if the same (user, plan, exercise) exists, update sets/reps/day
        workout.findByUserIdAndWorkoutPlanNameAndExerciseName(
                payload.getUserId(), payload.getWorkoutPlanName(), payload.getExerciseName()
        ).ifPresentOrElse(existing -> {
            existing.setDay(payload.getDay());
            existing.setSets(payload.getSets());
            existing.setReps(payload.getReps());
            workout.save(existing);
        }, () -> workout.save(payload));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/{planName}/exercises")
    public List<WorkoutExerciseDTO> getExercisesForDay(
            @PathVariable String userId,
            @PathVariable("planName") String workoutPlanName,
            @RequestParam String day
    ) {
        return workout.findByUserIdAndWorkoutPlanNameAndDay(userId, workoutPlanName, day)
                .stream()
                .map(w -> new WorkoutExerciseDTO(w.getExerciseName(), w.getSets(), w.getReps()))
                .toList();
    }




    public record WorkoutExerciseDTO(String name, int sets, int reps) {}
}
