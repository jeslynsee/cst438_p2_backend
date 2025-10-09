package com.example.demo.repository;

import com.example.demo.model.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository gives you CRUD operations automatically
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Long> {
}
