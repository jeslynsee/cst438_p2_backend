// WorkoutControllerTest.java
package com.example.demo.workout;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutController.class)
@org.springframework.context.annotation.Import(GlobalExceptionAdvice.class)
class WorkoutControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean WorkoutRepository workoutRepo;

    private Workout make(Long userId, String plan, String exercise, String day, int sets, int reps) {
        Workout w = new Workout();
        w.setId(1L);
        w.setUserId(userId);
        w.setWorkoutPlanName(plan);
        w.setExerciseName(exercise);
        w.setDay(day);
        w.setSets(sets);
        w.setReps(reps);
        return w;
    }

    @Test
    void getAllWorkouts_returnsList() throws Exception {
        var w1 = make(2L, "Plan A", "Pushup", "Mon", 3, 12);
        var w2 = make(2L, "Plan A", "Squat",  "Wed", 4, 10);
        given(workoutRepo.findAll()).willReturn(List.of(w1, w2));

        mvc.perform(get("/api/workout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exerciseName").value("Pushup"))
                .andExpect(jsonPath("$[1].exerciseName").value("Squat"));
    }

    @Test
    void getWorkoutById_found_returnsWorkout() throws Exception {
        var w = make(2L, "Plan A", "Pushup", "Mon", 3, 12);
        w.setId(1L);

        // Stub the correct repo method
        given(workoutRepo.findById(1L)).willReturn(Optional.of(w));

        mvc.perform(get("/api/workout/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.exerciseName").value("Pushup"));
    }

    @Test
    void getWorkoutById_notFound_returns404() throws Exception{
        given(workoutRepo.findById(42L)).willReturn(Optional.empty());

        mvc.perform(get("/api/workout/42"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Workout not found: 42"));
    }

    @Test
    void addExercise_insertsWhenNotExists() throws Exception {
        var payload = make(2L, "Plan A", "Pushup", "Mon", 3, 12);
        // simulate "not exists"
        given(workoutRepo.findByUserIdAndWorkoutPlanNameAndExerciseName(2L,"Plan A","Pushup"))
                .willReturn(Optional.empty());

        mvc.perform(post("/api/workout/add-exercise")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        // verify it saved a brand new Workout
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        verify(workoutRepo, times(1)).save(captor.capture());
        Workout saved = captor.getValue();
        // validate fields mapped
        org.junit.jupiter.api.Assertions.assertEquals(2L, saved.getUserId());
        org.junit.jupiter.api.Assertions.assertEquals("Plan A", saved.getWorkoutPlanName());
        org.junit.jupiter.api.Assertions.assertEquals("Pushup", saved.getExerciseName());
        org.junit.jupiter.api.Assertions.assertEquals("Mon", saved.getDay());
        org.junit.jupiter.api.Assertions.assertEquals(3, saved.getSets());
        org.junit.jupiter.api.Assertions.assertEquals(12, saved.getReps());
    }

    @Test
    void addExercise_updatesWhenExists() throws Exception {
        var payload = make(2L, "Plan A", "Pushup", "Wed", 5, 8);

        // simulate exists with different values to ensure update path is used
        var existing = make(2L, "Plan A", "Pushup", "Mon", 3, 12);
        given(workoutRepo.findByUserIdAndWorkoutPlanNameAndExerciseName(2L,"Plan A","Pushup"))
                .willReturn(Optional.of(existing));

        mvc.perform(post("/api/workout/add-exercise")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());

        // verify that repo.save(existing) was called with updated fields
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        verify(workoutRepo, times(1)).save(captor.capture());
        Workout saved = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("Wed", saved.getDay());
        org.junit.jupiter.api.Assertions.assertEquals(5, saved.getSets());
        org.junit.jupiter.api.Assertions.assertEquals(8, saved.getReps());
    }

    @Test
    void getExercisesForDay_returnsMappedDTOs() throws Exception {
        var w1 = make(2L, "Plan A", "Pushup", "Mon", 3, 12);
        var w2 = make(2L, "Plan A", "Squat",  "Mon", 4, 10);

        given(workoutRepo.findByUserIdAndWorkoutPlanNameAndDay(2L, "Plan A", "Mon"))
                .willReturn(List.of(w1, w2));

        mvc.perform(get("/api/workout/{userId}/{planName}/exercises", 2L, "Plan A")
                        .param("day", "Mon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pushup"))
                .andExpect(jsonPath("$[0].sets").value(3))
                .andExpect(jsonPath("$[0].reps").value(12))
                .andExpect(jsonPath("$[1].name").value("Squat"))
                .andExpect(jsonPath("$[1].sets").value(4))
                .andExpect(jsonPath("$[1].reps").value(10));
    }
    @Test
    void deleteWorkoutById_existingId_returns204() throws Exception {
        given(workoutRepo.existsById(5L)).willReturn(true);

        mvc.perform(delete("/api/workout/delete/5"))
                .andExpect(status().isNoContent());

        verify(workoutRepo).deleteById(5L);
    }

    @Test
    void deleteWorkoutById_notExisting_returns404() throws Exception {
        given(workoutRepo.existsById(99L)).willReturn(false);

        mvc.perform(delete("/api/workout/delete/99"))
                .andExpect(status().isNotFound());

        verify(workoutRepo, never()).deleteById(any());
    }
    @Test
    void deleteWorkoutPlan_existing_returns204() throws Exception {
        given(workoutRepo.existsByUserIdAndWorkoutPlanName(2L, "PlanA"))
                .willReturn(true);
        mvc.perform(delete("/api/workout/delete/2/PlanA"))
                .andExpect(status().isNoContent());

        verify(workoutRepo).deleteByUserIdAndWorkoutPlanName(2L, "PlanA");
    }

    @Test
    void deleteWorkoutPlan_notExisting_returns404() throws Exception {
        given(workoutRepo.existsByUserIdAndWorkoutPlanName(2L, "PlanA"))
                .willReturn(false);

        mvc.perform(delete("/api/workout/delete/u1/PlanA"))
                .andExpect(status().isNotFound());

        verify(workoutRepo, never()).deleteByUserIdAndWorkoutPlanName(any(), any());
    }
}