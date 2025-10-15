package com.example.demo;

import com.example.demo.controller.ExerciseController;
import com.example.demo.model.ExerciseEntity;
import com.example.demo.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

// static imports allow us to use methods without calling class name each time (e.g. Class.method() vs just method())
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;



@WebMvcTest(ExerciseController.class) // class/file we are testing 
public class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // MockBean deprecated but other one doesn't work with SB 3.5.6
    @MockBean
    private ExerciseRepository exerciseRepository; // mocks an instance of our exercise repo, so we can test in code

    @Test // defining test below for getting all exercises route
    void getAllExercises() throws Exception {
        ExerciseEntity exercise = new ExerciseEntity(); // initialize an exercise object
        // pretend to define a row in exercise table 
        exercise.setId(1L);
        exercise.setName("bench press");
        exercise.setType("strength");
        exercise.setMuscle("chest");
        exercise.setEquipment("barbell");
        exercise.setDifficulty("intermediate");
        exercise.setInstructions("lie on a bench with feet flat. grip the bar slightly wider than shoulder-width...");

        // mock doing a return of all the rows in exercise table, which should return our exercise
        when(exerciseRepository.findAll()).thenReturn(List.of(exercise));

        // does a GET request, mocking a call to our API for the route "/exercises"
        mockMvc.perform(get("/exercises"))
        .andExpect(status().isOk()) // expecting no HTTP errors
        .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // expecting data in JSON format
        // using jsonPath below to check all fields of our exercise object we made above with expected values
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("bench press"))
        .andExpect(jsonPath("$[0].type").value("strength"))
        .andExpect(jsonPath("$[0].muscle").value("chest"))
        .andExpect(jsonPath("$[0].equipment").value("barbell"))
        .andExpect(jsonPath("$[0].difficulty").value("intermediate"))
        .andExpect(jsonPath("$[0].instructions").value("lie on a bench with feet flat. grip the bar slightly wider than shoulder-width..."));

    }

    @Test // testing getting exercise by name route
    void getExerciseByName() throws Exception{
        ExerciseEntity exercise = new ExerciseEntity();

        exercise.setId(1L);
        exercise.setName("bench press");
        exercise.setType("strength");
        exercise.setMuscle("chest");
        exercise.setEquipment("barbell");
        exercise.setDifficulty("intermediate");
        exercise.setInstructions("lie on a bench with feet flat. grip the bar slightly wider than shoulder-width...");

        when(exerciseRepository.findByName(org.mockito.ArgumentMatchers.anyString())) // argument here ensures whatever string used is match to return list of exercise
        .thenReturn(List.of(exercise));

        // when mocking this route for GET method, getting exercise details by name, I want to make sure we get OK HTTP response, JSON- 
        // formatted data, id and name for starters, and then instructions 
        String name = "bench press";
        mockMvc.perform(get("/exercises/{name}", name))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].instructions").exists());

    }

    @Test // testing POST route for creating exercise
    void createExercise() throws Exception {
     
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setId(1L);
        exercise.setName("push up");
        exercise.setType("strength");
        exercise.setMuscle("chest");
        exercise.setEquipment("none");
        exercise.setDifficulty("beginner");
        exercise.setInstructions("start in a plank position and lower your chest to the floor, then push back up.");

  
        when(exerciseRepository.save(org.mockito.ArgumentMatchers.any(ExerciseEntity.class)))
        .thenReturn(exercise);

        // JSON data to send in POST body
        // using triple quote below for text block instead of concatenating everything together
        String jsonBody = """
            {
                "name": "push up",
                "type": "strength",
                "muscle": "chest",
                "equipment": "none",
                "difficulty": "beginner",
                "instructions": "start in a plank position and lower your chest to the floor, then push back up."
            }
            """;

        mockMvc.perform(post("/exercises")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("push up"))
        .andExpect(jsonPath("$.muscle").value("chest"))
        .andExpect(jsonPath("$.difficulty").value("beginner"));
    }


    @Test // testing PUT route updating an exercise
    void updateExercise() throws Exception {
        Long id = 1L;

        ExerciseEntity oldExercise = new ExerciseEntity();
        oldExercise.setId(id);
        oldExercise.setName("bench press");
        oldExercise.setType("strength");
        oldExercise.setMuscle("chest");
        oldExercise.setEquipment("barbell");
        oldExercise.setDifficulty("intermediate");
        oldExercise.setInstructions("old instructions");

        ExerciseEntity updatedExercise = new ExerciseEntity();
        updatedExercise.setId(id);
        updatedExercise.setName("bench press");
        updatedExercise.setType("strength");
        updatedExercise.setMuscle("chest");
        updatedExercise.setEquipment("barbell");
        updatedExercise.setDifficulty("advanced");
        updatedExercise.setInstructions("new updated instructions");

        when(exerciseRepository.findById(id)).thenReturn(java.util.Optional.of(oldExercise));
        when(exerciseRepository.save(org.mockito.ArgumentMatchers.any(ExerciseEntity.class)))
        .thenReturn(updatedExercise);
        // using triple quote below for text block instead of concatenating everything together
        String jsonBody = """ 
            {
                "name": "bench press",
                "type": "strength",
                "muscle": "chest",
                "equipment": "barbell",
                "difficulty": "advanced",
                "instructions": "new updated instructions"
            }
            """;

        mockMvc.perform(put("/exercises/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.difficulty").value("advanced"))
        .andExpect(jsonPath("$.instructions").value("new updated instructions"));
    }

    @Test // testing delete method
    void deleteExercise() throws Exception {
        Long id = 1L;

        // no return value, only making sure it's called
        doNothing().when(exerciseRepository).deleteById(id);

        mockMvc.perform(delete("/exercise/{id}", id))
        .andExpect(status().isOk());
    }



}
