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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
