package com.example.demo.repository;

import com.example.demo.model.ExerciseEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository gives you CRUD operations automatically
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Long> {

    // Custom CRUD operations interface definition here

    // custom finding exercise details by name, so defining here. this gets translated to something like SELECT..from..WHERE name = ""
    List<ExerciseEntity> findByName(String name);
}
