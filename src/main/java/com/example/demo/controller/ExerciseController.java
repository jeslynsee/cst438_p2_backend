package com.example.demo.controller;

import com.example.demo.model.ExerciseEntity;
import com.example.demo.repository.ExerciseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExerciseController {

    private final ExerciseRepository repository;

    // Constructor-based dependency injection
    public ExerciseController(ExerciseRepository repository) {
        this.repository = repository;
    }

    // Aggregate Root; taken from Dr. C's example Books code
    // Gets all rows from the exercises table
    @GetMapping("/exercises")
    public List<ExerciseEntity> getAll() {
        return repository.findAll();
    }

    // Get exercise details by name
    @GetMapping("/exercises/{name}")
    List<ExerciseEntity> getExerciseByName(@PathVariable String name) {
        return repository.findByName(name);
    }

    // Inserts a new row into exercises table
    // @RequestBody says to take JSON data from req. body and feed it to method, turning it into a row in table
    @PostMapping("/exercises")
    public ExerciseEntity createExercise(@RequestBody ExerciseEntity exercise) {
        return repository.save(exercise);
    }

    // Update an exercise's fields, including name, type, muscle, equipment, difficulty, instructions
    // Put method also based on Dr. C's book example code
    // using Req body annotation so we can grab JSON data of new exercise and feed method to turn into a row in table
    // using Path var. annotation so we can grab id from URL path and turn into actual param in method to find exercise by ID
    @PutMapping("/exercises/{id}")
    ExerciseEntity updateExercise(@RequestBody ExerciseEntity newExercise, @PathVariable Long id) {
        return repository.findById(id).map(exercise -> {
            exercise.setName(newExercise.getName());
            exercise.setType(newExercise.getType());
            exercise.setMuscle(newExercise.getMuscle()); 
            exercise.setEquipment(newExercise.getEquipment());
            exercise.setDifficulty(newExercise.getDifficulty());
            exercise.setInstructions(newExercise.getInstructions());
            return repository.save(exercise);
          })
          .orElseGet(() -> {
            return repository.save(newExercise);
          });
    }

    // Delete an exercise/row from exercises table 
    //@PathVariable annotation allows for grabbing value from URL path and connecting it to method parameter 
    @DeleteMapping("/exercise/{id}")
    void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

